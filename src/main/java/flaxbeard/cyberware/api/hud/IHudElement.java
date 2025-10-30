package flaxbeard.cyberware.api.hud;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;

public interface IHudElement {
    enum EnumAnchorHorizontal {
        LEFT,
        RIGHT
    }

    enum EnumAnchorVertical {
        TOP,
        BOTTOM
    }

    void render(PlayerEntity entityPlayer, ScaledResolution resolution, boolean isHUDjackAvailable, boolean isConfigOpen, float partialTicks, MatrixStack matrixStack);

    boolean canMove();

    void setX(int x);
    void setY(int y);
    int getX();
    int getY();

    int getWidth();
    int getHeight();

    boolean canHide();
    void setHidden(boolean hidden);
    boolean isHidden();

    EnumAnchorHorizontal getHorizontalAnchor();
    void setHorizontalAnchor(EnumAnchorHorizontal anchor);

    EnumAnchorVertical getVerticalAnchor();
    void setVerticalAnchor(EnumAnchorVertical anchor);

    void reset();

    String getUniqueName();

    void save(IHudSaveData data);
    void load(IHudSaveData data);
}
