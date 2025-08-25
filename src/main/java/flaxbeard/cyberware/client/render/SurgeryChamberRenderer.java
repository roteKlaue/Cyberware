package flaxbeard.cyberware.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.common.block.SurgeryChamberBlock;
import flaxbeard.cyberware.common.block.entities.SurgeryChamberBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class SurgeryChamberRenderer extends TileEntityRenderer<SurgeryChamberBlockEntity> {
    private static final SurgeryChamberModel MODEL = new SurgeryChamberModel();
    private static final ResourceLocation TEXTURE = new ResourceLocation(OverclockedOrgans.MOD_ID, "textures/models/surgery_chamber_door.png");

    public SurgeryChamberRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(SurgeryChamberBlockEntity te, float partialTicks, MatrixStack ms,
                       IRenderTypeBuffer buffer, int light, int overlay) {
        if (te == null || te.getLevel() == null) return;

        BlockState state = te.getLevel().getBlockState(te.getBlockPos());
        if (!(state.getBlock() instanceof SurgeryChamberBlock)) return;

        if (state.getValue(SurgeryChamberBlock.HALF) != DoubleBlockHalf.UPPER) return;

        ms.pushPose();
        ms.translate(0.5, 0.5, 0.5);

        Direction facing = state.getValue(SurgeryChamberBlock.FACING);
        switch (facing) {
            case EAST:  ms.mulPose(Vector3f.YP.rotationDegrees(270f)); break;
            case WEST:  ms.mulPose(Vector3f.YP.rotationDegrees(90f)); break;
            case SOUTH: ms.mulPose(Vector3f.YP.rotationDegrees(180f)); break;
            case NORTH:
            default: break;
        }

        boolean isOpen = state.getValue(SurgeryChamberBlock.OPEN);
        float ticksBase = Minecraft.getInstance().player != null
                ? Minecraft.getInstance().player.tickCount
                : 0f;
        float ticks = ticksBase + partialTicks;

        if (isOpen != te.lastOpen) {
            te.lastOpen = isOpen;
            te.openTicks = ticks;
        }

        float t = Math.min(10f, ticks - te.openTicks);
        float s = (float) Math.sin(t * ((Math.PI / 2f) / 10f)) * 90f;
        float rotate = isOpen ? s : (90f - s);

        IVertexBuilder vb = buffer.getBuffer(RenderType.entityCutout(TEXTURE));

        ms.pushPose();
        ms.translate(-6f / 16f, 0f, -6f / 16f);
        ms.mulPose(Vector3f.YP.rotationDegrees(-rotate));
        MODEL.left.render(ms, vb, light, overlay, 1f, 1f, 1f, 1f);
        ms.popPose();

        ms.pushPose();
        ms.translate( 6f / 16f, 0f, -6f / 16f);
        ms.mulPose(Vector3f.YP.rotationDegrees(rotate));
        MODEL.right.render(ms, vb, light, overlay, 1f, 1f, 1f, 1f);
        ms.popPose();

        ms.popPose();
    }
}
