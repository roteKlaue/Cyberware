package flaxbeard.cyberware.client.gui.hud.notification;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.hud.INotification;
import flaxbeard.cyberware.client.ClientUtils;
import flaxbeard.cyberware.common.handler.HudHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NotificationArmor implements INotification {
    private final boolean light;

    public NotificationArmor(boolean light) {
        this.light = light;
    }

    @Override
    public void render(MatrixStack mx, int x, int y) {
        Minecraft.getInstance().getTextureManager().bind(HudHandler.HUD_TEXTURE);

        mx.pushPose();
        float[] color = CyberwareAPI.getHUDColor();
        RenderSystem.color3f(color[0], color[1], color[2]);
        ClientUtils.drawTexturedModalRect(mx, x, y + 1, 0, 25, 15, 14);
        mx.popPose();

        if (light) {
            ClientUtils.drawTexturedModalRect(mx,x + 9, y + 1 + 7, 15, 25, 7, 9);
            return;
        }
        ClientUtils.drawTexturedModalRect(mx,x + 8, y + 1 + 7, 22, 25, 8, 9);
    }

    @Override
    public int getDuration() {
        return 20;
    }
}
