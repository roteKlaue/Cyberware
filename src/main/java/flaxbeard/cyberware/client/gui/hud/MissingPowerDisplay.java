package flaxbeard.cyberware.client.gui.hud;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.ICyberwareUserData;
import flaxbeard.cyberware.api.hud.BaseHudElement;
import flaxbeard.cyberware.api.hud.ScaledResolution;
import flaxbeard.cyberware.common.handler.HudHandler;
import flaxbeard.cyberware.common.item.CyberwareItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MissingPowerDisplay extends BaseHudElement {
    private static final List<ItemStack> exampleStacks = new ArrayList<>();

    public MissingPowerDisplay() {
        super("cyberware:missing_power");
        setDefaultX(-15);
        setDefaultY(35);
        setWidth(16 + 20);
        setHeight(18 * 8);
    }

    @Override
    public void renderElement(int x, int y, PlayerEntity player, ScaledResolution resolution, boolean hudjackAvailable, boolean configOpen, float partialTicks, MatrixStack mx) {
        if (isHidden() || !hudjackAvailable) return;

        if (exampleStacks.isEmpty()) {
            for (int i = 0; i < 8; i++) {
                exampleStacks.add(new ItemStack(CyberwareItems.CYBER_EYES_MANUFACTURED.get()));
            }
        }

        LazyOptional<ICyberwareUserData> lazy = CyberwareAPI.getCyberwareData(player);
        if (!lazy.isPresent()) return;
        ICyberwareUserData cyberwareUserData = lazy.orElseThrow(IllegalStateException::new);

        boolean isRightAnchored = getHorizontalAnchor() == EnumAnchorHorizontal.RIGHT;
        float currTime = player.tickCount + partialTicks;

        Minecraft mc = Minecraft.getInstance();
        FontRenderer fontRenderer = mc.font;
        ItemRenderer itemRenderer = mc.getItemRenderer();

        List<ItemStack> stacksPowerOutage = configOpen ? exampleStacks : cyberwareUserData.getPowerOutages();
        List<Integer> timesPowerOutage = cyberwareUserData.getPowerOutageTimes();
        if (configOpen) timesPowerOutage = Collections.emptyList();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        mc.getTextureManager().bind(HudHandler.HUD_TEXTURE);

        int xPosition = x - 1 + (isRightAnchored ? 0 : 20);
        List<Integer> indexesElapsed = new ArrayList<>();

        for (int index = stacksPowerOutage.size() - 1; index >= 0; index--) {
            ItemStack stack = stacksPowerOutage.get(index);
            if (stack == null || stack.isEmpty()) continue;

            int time;
            if (configOpen) {
                time = (int) (currTime - 20 - ((player.tickCount + index * 6) % 40));
            } else {
                if (index >= timesPowerOutage.size()) continue;
                time = timesPowerOutage.get(index);
            }

            int age = player.tickCount - time;
            if (age < 50) {
                double percentVisible = Math.max(0F, (currTime - time - 20) / 30F);
                float xOffset = (float) (20F * Math.sin(percentVisible * Math.PI / 2F));
                float alpha = (float) Math.min(1.0, percentVisible);

                int yPosition = y + (stacksPowerOutage.size() - 1 - index) * 18;
                float yJitter = (float)((Math.random() - 0.5) * 2);

                mx.pushPose();
                int alphaColor = ((int)(alpha * 255) << 24) | 0xFF0000;
                fontRenderer.drawShadow(mx, "!", xPosition + 14, yPosition + 8, alphaColor);
                mx.popPose();

                mx.pushPose();
                mx.translate(isRightAnchored ? xOffset : -xOffset, yJitter, 0.0F);
                RenderHelper.setupFor3DItems();
                RenderSystem.color4f(1f, 1f, 1f, alpha);
                itemRenderer.renderGuiItem(stack, xPosition, yPosition);
                itemRenderer.renderGuiItemDecorations(fontRenderer, stack, xPosition, yPosition, null);
                RenderHelper.setupForFlatItems();
                mx.popPose();
            } else if (!configOpen) {
                indexesElapsed.add(index);
            }
        }

        if (!indexesElapsed.isEmpty() && !configOpen) {
            indexesElapsed.sort(Collections.reverseOrder());
            for (int idx : indexesElapsed) {
                if (idx >= 0 && idx < cyberwareUserData.getPowerOutages().size()) {
                    cyberwareUserData.getPowerOutages().remove(idx);
                }
                if (idx >= 0 && idx < cyberwareUserData.getPowerOutageTimes().size()) {
                    cyberwareUserData.getPowerOutageTimes().remove(idx);
                }
            }
        }

        RenderSystem.disableBlend();
    }
}
