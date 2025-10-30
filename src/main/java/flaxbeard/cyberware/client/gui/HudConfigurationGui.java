package flaxbeard.cyberware.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.api.hud.CyberwareHudDataEvent;
import flaxbeard.cyberware.api.hud.CyberwareHudEvent;
import flaxbeard.cyberware.api.hud.IHudElement;
import flaxbeard.cyberware.api.hud.ScaledResolution;
import flaxbeard.cyberware.api.item.IHudjack;
import flaxbeard.cyberware.client.ClientUtils;
import flaxbeard.cyberware.client.gui.hud.HudNBTData;
import flaxbeard.cyberware.common.handler.HudHandler;
import flaxbeard.cyberware.common.network.CyberwarePackets;
import flaxbeard.cyberware.common.network.SyncHudDataPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import java.util.List;

public class HudConfigurationGui extends Screen {
    private IHudElement dragging = null;
    private IHudElement hoveredElement = null;
    private int offsetX = 0;
    private int offsetY = 0;
    private boolean clicked = false;

    @Override
    public void render(@Nonnull MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        super.render(ms, mouseX, mouseY, partialTicks);

        Minecraft mc = Minecraft.getInstance();
        ScaledResolution resolution = ScaledResolution.of(mc.getWindow());

        boolean active = false;
        LazyOptional<ICyberwareUserData> cyberwareUserData = CyberwareAPI.getCyberwareData(mc.player);

        if (cyberwareUserData.isPresent()) {
            for (ItemStack stack : cyberwareUserData.orElseThrow(RuntimeException::new).getHudjackItems()) {
                if (((IHudjack) CyberwareAPI.getCyberware(stack)).isActive(stack)) {
                    active = true;
                    break;
                }
            }
        }

        CyberwareHudEvent hudEvent = new CyberwareHudEvent(resolution, active);
        MinecraftForge.EVENT_BUS.post(hudEvent);
        List<IHudElement> elements = hudEvent.getElements();

        hoveredElement = null;
        for (IHudElement element : elements) {
            if (hoveredElement == null && dragging == null) {
                int x = getAbsoluteX(resolution, element);
                int y = getAbsoluteY(resolution, element);

                if (isPointInRegion(x, y, element.getWidth(), element.getHeight(), mouseX, mouseY)) {
                    hoveredElement = element;
                    offsetX = mouseX - x;
                    offsetY = mouseY - y;
                }
            }
        }

        for (IHudElement element : elements) {
            drawBox(ms, resolution, element, mouseX, mouseY);
            drawButtons(ms, resolution, element, mouseX, mouseY);
        }

        if (dragging != null) {
            int moveToX = mouseX - offsetX;
            int moveToY = mouseY - offsetY;

            setXFromAbsolute(resolution, dragging, moveToX);
            setYFromAbsolute(resolution, dragging, moveToY);

            if (mc.player.isCrouching()) {
                dragging.setX(Math.round(dragging.getX() / 5F) * 5);
                dragging.setY(Math.round(dragging.getY() / 5F) * 5);
            }
        }

        clicked = false;
    }

    private boolean isPointInRegion(int rectX, int rectY, int rectW, int rectH, int pX, int pY) {
        return pX >= rectX - 1 && pX < rectX + rectW + 1 && pY >= rectY - 1 && pY < rectY + rectH + 1;
    }

    private void drawBox(MatrixStack ms, ScaledResolution resolution, IHudElement e, int mouseX, int mouseY) {
        Minecraft.getInstance().getTextureManager().bind(HudHandler.HUD_TEXTURE);
        RenderSystem.enableBlend();

        int elemX = getAbsoluteX(resolution, e) - 1;
        int elemY = getAbsoluteY(resolution, e) - 1;

        float[] c = CyberwareAPI.getHUDColor();
        RenderSystem.color4f(c[0], c[1], c[2], 1f);

        ClientUtils.renderBorder(ms, elemX, elemY, e.getWidth(), e.getHeight());

        RenderSystem.color4f(1f, 1f, 1f, 1f);
        RenderSystem.disableBlend();
    }

    private void drawButtons(MatrixStack ms, ScaledResolution resolution, IHudElement element, int mouseX, int mouseY) {
        Minecraft.getInstance().getTextureManager().bind(HudHandler.HUD_TEXTURE);

        int elemX = getAbsoluteX(resolution, element) - 1;
        int elemY = getAbsoluteY(resolution, element) - 1;

        int buttonsY = (elemY + element.getHeight() + 10 > resolution.height)
                ? elemY - 11 : (elemY + element.getHeight() + 4);
        int buttonsX = elemX + 5;

        // SHOW/HIDE
        if (element.canHide()) {
            boolean hover = isPointInRegion(buttonsX, buttonsY, 9, 9, mouseX, mouseY);
            boolean hidden = element.isHidden();
            ClientUtils.draw(ms, buttonsX, buttonsY, hover ^ hidden ? 125 : 116, 0, 9, 9);
            buttonsX += 11;
        }
    }


    public HudConfigurationGui() {
        super(new StringTextComponent("HUD Configuration"));
    }

    public static int getAbsoluteX(ScaledResolution resolution, IHudElement e) {
        if (e.getHorizontalAnchor() == IHudElement.EnumAnchorHorizontal.RIGHT)
            return resolution.width - e.getX() - e.getWidth();
        return e.getX();
    }

    public static int getAbsoluteY(ScaledResolution resolution, IHudElement e) {
        if (e.getVerticalAnchor() == IHudElement.EnumAnchorVertical.BOTTOM)
            return resolution.height - e.getY() - e.getHeight();
        return e.getY();
    }

    public static void setXFromAbsolute(ScaledResolution resolution, IHudElement e, int x) {
        if (e.getHorizontalAnchor() == IHudElement.EnumAnchorHorizontal.RIGHT)
            e.setX(resolution.width - x - e.getWidth());
        else e.setX(x);
    }

    public static void setYFromAbsolute(ScaledResolution resolution, IHudElement e, int y) {
        if (e.getVerticalAnchor() == IHudElement.EnumAnchorVertical.BOTTOM)
            e.setY(resolution.height - y - e.getHeight());
        else e.setY(y);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && dragging == null) {
            dragging = hoveredElement;
            clicked = true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && dragging != null) dragging = null;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() { return false; }

    @Override
    public void onClose() {
        CompoundNBT tag = new CompoundNBT();

        CyberwareHudDataEvent evt = new CyberwareHudDataEvent();
        MinecraftForge.EVENT_BUS.post(evt);

        for (IHudElement e : evt.getElements()) {
            HudNBTData data = new HudNBTData(new CompoundNBT());
            e.save(data);
            tag.put(e.getUniqueName(), data.getTag());
        }

        LazyOptional<ICyberwareUserData> user = CyberwareAPI.getCyberwareData(Minecraft.getInstance().player);
        if (user.isPresent()) user.orElseThrow(RuntimeException::new).setHudData(tag);

        CyberwarePackets.NETWORK.sendToServer(new SyncHudDataPacket(tag));
    }
}
