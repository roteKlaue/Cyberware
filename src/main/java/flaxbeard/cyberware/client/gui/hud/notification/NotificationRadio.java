package flaxbeard.cyberware.client.gui.hud.notification;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.hud.INotification;
import flaxbeard.cyberware.client.ClientUtils;
import flaxbeard.cyberware.common.handler.HudHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NotificationRadio implements INotification {
    private final int tier;

    public NotificationRadio(int tier) {
        this.tier = tier;
    }

    @Override
    public void render(MatrixStack stack, int x, int y) {
        Minecraft.getInstance().getTextureManager().bind(HudHandler.HUD_TEXTURE);
        float[] color = CyberwareAPI.getHUDColor();

        if (tier <= 0) {
            RenderSystem.color3f(color[0], color[1], color[2]);
            ClientUtils.drawTexturedModalRect(stack, x, y + 1, 28, 39, 15, 14);
            return;
        }

        stack.pushPose();
        RenderSystem.color3f(color[0], color[1], color[2]);
        ClientUtils.drawTexturedModalRect(stack, x, y + 1, 13, 39, 15, 14);
        stack.popPose();

        ITextComponent textRadioTier = tier == 1 ? new TranslationTextComponent("gui.overclockedorgans.radio_internal") : new StringTextComponent(Integer.toString(tier - 1));
        FontRenderer fontRenderer = Minecraft.getInstance().font;
        fontRenderer.drawShadow(stack, textRadioTier, x + 15 - fontRenderer.getSplitter().stringWidth(textRadioTier), y + 9, 0xFFFFFF);
    }

    @Override
    public int getDuration() {
        return 40;
    }
}
