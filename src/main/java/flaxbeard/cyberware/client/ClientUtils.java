package flaxbeard.cyberware.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Matrix4f;
import org.lwjgl.opengl.GL11;

public class ClientUtils {

    private static final float TEXTURE_SCALE = 1.0F / 256F;
    public static void drawTexturedModalRect(MatrixStack matrixStack, int x, int y, int textureX, int textureY, int width, int height) {
        Matrix4f matrix = matrixStack.last().pose();

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();

        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        bufferBuilder.vertex(matrix, x,         y + height, 0.0F)
                .uv(textureX * TEXTURE_SCALE, (textureY + height) * TEXTURE_SCALE)
                .endVertex();

        bufferBuilder.vertex(matrix, x + width, y + height, 0.0F)
                .uv((textureX + width) * TEXTURE_SCALE, (textureY + height) * TEXTURE_SCALE)
                .endVertex();

        bufferBuilder.vertex(matrix, x + width, y, 0.0F)
                .uv((textureX + width) * TEXTURE_SCALE, textureY * TEXTURE_SCALE)
                .endVertex();

        bufferBuilder.vertex(matrix, x, y, 0.0F)
                .uv(textureX * TEXTURE_SCALE, textureY * TEXTURE_SCALE)
                .endVertex();

        tessellator.end();
    }

    public static void renderBorder(MatrixStack ms, int elemX, int elemY, int width, int height) {
    }

    public static void draw(MatrixStack ms, int buttonsX, int buttonsY, int i, int i1, int i2, int i3) {
    }
}
