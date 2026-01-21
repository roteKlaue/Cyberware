package flaxbeard.cyberware.client.gui.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * An image-based button for GUIs.
 *
 * <p>This button renders a portion of a texture and switches to an alternate
 * texture region while pressed. It supports a custom press callback.</p>
 */
public class EngineeringImageButton extends AbstractButton {
    private final ResourceLocation texture;
    private final int texU, texV;
    private final int pressedU, pressedV;
    private final int texWidth, texHeight;
    private final Consumer<EngineeringImageButton> onPress;

    private boolean isPressed = false;

    /**
     * Creates a new image button.
     *
     * @param x         X position on screen
     * @param y         Y position on screen
     * @param width     Button width in pixels
     * @param height    Button height in pixels
     * @param u         Texture U coordinate for normal state
     * @param v         Texture V coordinate for normal state
     * @param pressedU Texture U coordinate for pressed state
     * @param pressedV Texture V coordinate for pressed state
     * @param texture  Texture atlas resource
     * @param texWidth Total texture width
     * @param texHeight Total texture height
     * @param onPress  Callback when the button is pressed
     */
    public EngineeringImageButton(int x, int y, int width, int height,
                                  int u, int v,
                                  int pressedU, int pressedV,
                                  ResourceLocation texture,
                                  int texWidth, int texHeight,
                                  Consumer<EngineeringImageButton> onPress) {
        super(x, y, width, height, StringTextComponent.EMPTY);
        this.texU = u;
        this.texV = v;
        this.pressedU = pressedU;
        this.pressedV = pressedV;
        this.texture = texture;
        this.texWidth = texWidth;
        this.texHeight = texHeight;
        this.onPress = onPress;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.active || !this.visible) return false;
        if (!this.isHovered) return false;
        if (button != 0) return false;

        this.isPressed = true;
        this.playDownSound(Minecraft.getInstance().getSoundManager());
        this.onPress();
        return true;
    }


    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) this.isPressed = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void onPress() {
        if (onPress == null) return;
        onPress.accept(this);
    }

    @Override
    public void renderButton(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Minecraft.getInstance().getTextureManager().bind(texture);

        int u = texU;
        int v = texV;

        if (isPressed) {
            u = pressedU;
            v = pressedV;
        }

        blit(matrixStack, this.x, this.y, u, v, this.width, this.height, texWidth, texHeight);
    }
}
