package flaxbeard.cyberware.api.hud;

import com.mojang.blaze3d.matrix.MatrixStack;

public interface INotification {
    void render(MatrixStack stack, int x, int y);
    int getDuration();
}
