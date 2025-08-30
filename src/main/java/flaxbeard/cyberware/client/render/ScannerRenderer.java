package flaxbeard.cyberware.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.common.block.ScannerBlock;
import flaxbeard.cyberware.common.block.entities.ScannerBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nonnull;

public class ScannerRenderer extends TileEntityRenderer<ScannerBlockEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(OverclockedOrgans.MOD_ID, "textures/models/scanner.png");
    private static final ScannerModel MODEL = new ScannerModel();

    public ScannerRenderer(TileEntityRendererDispatcher rendererDispatcher) {
        super(rendererDispatcher);
    }

    @Override
    public void render(@Nonnull ScannerBlockEntity te, float partialTicks, MatrixStack matrixStack,
                       @Nonnull IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {

        matrixStack.pushPose();
        matrixStack.translate(0.5, 0.5, 0.5);

        BlockState state = te.getBlockState();
        Direction facing = state.getValue(ScannerBlock.FACING);

        float yaw;
        switch (facing) {
            case SOUTH: yaw = 180F; break;
            case WEST:  yaw = 270F; break;
            case EAST:  yaw = 90F; break;
            default:    yaw = 0F;
        }

        matrixStack.mulPose(Vector3f.YP.rotationDegrees(yaw));

        ItemStack stack = te.slots.getStackInSlot(0);
        if (!stack.isEmpty()) {
            matrixStack.pushPose();
            matrixStack.translate(0.0F, -1.6F / 16F, 0.0F);
            matrixStack.scale(0.8F, 0.8F, 0.8F);
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(90F));

            ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
            itemRenderer.renderStatic(stack, ItemCameraTransforms.TransformType.NONE,
                    combinedLight, combinedOverlay, matrixStack, buffer);

            matrixStack.popPose();
        }

        int difference = Math.abs(te.x - te.lastX);
        float timeToTake = difference * 3;
        float time = Math.min(timeToTake, te.ticks + partialTicks - te.ticksMove);
        float progress = (float) Math.cos((Math.PI / 2) * (1F - (time / timeToTake)));
        if (difference == 0) progress = 1.0F;

        matrixStack.translate(0F, 0F, ((te.lastX + (te.x - te.lastX) * progress) + 1.5F) / 16F);

        IVertexBuilder vb = buffer.getBuffer(RenderType.entityCutout(TEXTURE));
        MODEL.renderBar(matrixStack, vb, combinedLight, OverlayTexture.NO_OVERLAY);

        int difference2 = Math.abs(te.z - te.lastZ);
        float timeToTake2 = difference2 * 3;
        float time2 = Math.max(0, Math.min(timeToTake2, te.ticks + partialTicks - te.ticksMove));
        float progress2 = (float) Math.cos((Math.PI / 2) * (1F - (time2 / timeToTake2)));
        if (difference2 == 0) progress2 = 1.0F;

        matrixStack.translate(((te.lastZ + (te.z - te.lastZ) * progress2) + .5F) / 16F, 0F, 0F);

        MODEL.renderScanner(matrixStack, vb, combinedLight, OverlayTexture.NO_OVERLAY);

        if (te.ticks > 0 && progress2 >= 1F && progress >= 1F && (((int) (te.ticks + partialTicks)) % 2F == 0)) {
            MODEL.renderBeam(matrixStack, vb, combinedLight, OverlayTexture.NO_OVERLAY);
        }

        matrixStack.popPose();
    }
}
