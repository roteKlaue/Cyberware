package flaxbeard.cyberware.api.hud;

import net.minecraft.client.MainWindow;
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

    void render(PlayerEntity entityPlayer, MainWindow resolution, boolean isHUDjackAvailable, boolean isConfigOpen, float partialTicks);

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
