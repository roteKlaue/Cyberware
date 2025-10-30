package flaxbeard.cyberware.api.hud;

import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.Setter;
import net.minecraft.entity.player.PlayerEntity;

public abstract class BaseHudElement implements IHudElement {
    private int defaultX = 0;
    private int defaultY = 0;
    private int x = 0;
    private int y = 0;
    @Setter
    private int width = 0;
    @Setter
    private int height = 0;
    private boolean hidden = false;
    private final String name;

    private EnumAnchorHorizontal defaultHAnchor = EnumAnchorHorizontal.LEFT;
    private EnumAnchorVertical defaultVAnchor = EnumAnchorVertical.TOP;
    private EnumAnchorHorizontal hAnchor = EnumAnchorHorizontal.LEFT;
    private EnumAnchorVertical vAnchor = EnumAnchorVertical.TOP;

    public BaseHudElement(String name) {
        this.name = name;
    }

    @Override
    public void render(PlayerEntity player, ScaledResolution resolution, boolean isHUDjackAvailable, boolean isConfigOpen, float partialTicks, MatrixStack mx) {
        int x = getX();
        int y = getY();
        if (getHorizontalAnchor() == EnumAnchorHorizontal.RIGHT) {
            x = resolution.width - x - getWidth();
        }
        if (getVerticalAnchor() == EnumAnchorVertical.BOTTOM) {
            y = resolution.height - y - getHeight();
        }

        renderElement(x, y, player, resolution, isHUDjackAvailable, isConfigOpen, partialTicks, mx);
    }

    public abstract void renderElement(int x, int y, PlayerEntity player, ScaledResolution resolution, boolean hudjackAvailable, boolean configOpen, float partialTicks, MatrixStack mx);

    public void setDefaultX(int x) {
        this.defaultX = x;
        setX(x);
    }

    public void setDefaultY(int y) {
        this.defaultY = y;
        setY(y);
    }

    @Override
    public boolean canMove() {
        return true;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public boolean canHide() {
        return true;
    }

    @Override
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public EnumAnchorHorizontal getHorizontalAnchor() {
        return hAnchor;
    }

    public void setDefaultHorizontalAnchor(EnumAnchorHorizontal anchor) {
        defaultHAnchor = anchor;
        setHorizontalAnchor(anchor);
    }

    @Override
    public void setHorizontalAnchor(EnumAnchorHorizontal anchor) {
        hAnchor = anchor;
    }

    @Override
    public EnumAnchorVertical getVerticalAnchor() {
        return vAnchor;
    }

    public void setDefaultVerticalAnchor(EnumAnchorVertical anchor) {
        defaultVAnchor = anchor;
        setVerticalAnchor(anchor);
    }

    @Override
    public void setVerticalAnchor(EnumAnchorVertical anchor) {
        vAnchor = anchor;
    }

    @Override
    public void reset() {
        x = defaultX;
        y = defaultY;
        vAnchor = defaultVAnchor;
        hAnchor = defaultHAnchor;
    }

    @Override
    public String getUniqueName() {
        return name;
    }

    @Override
    public void save(IHudSaveData data) {
        data.setInteger("x", x);
        data.setInteger("y", y);
        data.setBoolean("top", vAnchor == EnumAnchorVertical.TOP);
        data.setBoolean("left", hAnchor == EnumAnchorHorizontal.LEFT);
    }

    @Override
    public void load(IHudSaveData data) {
        x = data.getInteger("x");
        y = data.getInteger("y");
        vAnchor = data.getBoolean("top") ? EnumAnchorVertical.TOP : EnumAnchorVertical.BOTTOM;
        hAnchor = data.getBoolean("left") ? EnumAnchorHorizontal.LEFT : EnumAnchorHorizontal.RIGHT;
    }
}
