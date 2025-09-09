package flaxbeard.cyberware.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.common.block.CyberwareBlocks;
import flaxbeard.cyberware.common.block.DirectionalBlock;
import flaxbeard.cyberware.common.block.entities.EngineeringTableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;

public class EngineeringTableRenderer extends TileEntityRenderer<EngineeringTableBlockEntity> {
    private static final EngineeringTableModel model = new EngineeringTableModel();
    private static final ResourceLocation TEXTURE = new ResourceLocation(OverclockedOrgans.MOD_ID + ":textures/models/engineering.png");

    public EngineeringTableRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(@Nonnull EngineeringTableBlockEntity te, float partialTicks,
                       @Nonnull MatrixStack ms, @Nonnull IRenderTypeBuffer buffer,
                       int combinedLight, int combinedOverlay) {
        if (te.getLevel() == null) return;

        BlockState state = te.getBlockState();
        if (state.getBlock() != CyberwareBlocks.ENGINEERING_TABLE_BLOCK.getBlock().get()) return;

        ms.pushPose();
        ms.translate(0.5D, 0.5D, 0.5D);

        long worldTime = te.getLevel().getGameTime();
        float timeElapsed = MathHelper.clamp((worldTime - te.clickedTime) + partialTicks, 0F, 22F);

        boolean showIcon = true;
        float amount;

        if (timeElapsed < 2F) {
            amount = timeElapsed / 2F;
        } else {
            timeElapsed -= 2F;

            if (timeElapsed < 15F) {
                showIcon = false;
            }

            amount = 1F - (timeElapsed / 20F);
        }

        ms.pushPose();
        ms.translate(0.0D, amount * (-6F / 16F), 0.0D);
        IVertexBuilder vb = buffer.getBuffer(RenderType.entityCutout(TEXTURE));
        model.renderToBuffer(ms, vb, combinedLight, combinedOverlay, 1f, 1f, 1f, 1f);
        ms.popPose();

        ItemStack stack = te.slots.getStackInSlot(0);
        if (!stack.isEmpty() && showIcon) {
            ms.pushPose();

            Direction facing = state.getValue(DirectionalBlock.FACING);
            switch (facing) {
                case EAST:  ms.mulPose(Vector3f.YP.rotationDegrees(270f)); break;
                case WEST:  ms.mulPose(Vector3f.YP.rotationDegrees(90f)); break;
                case SOUTH: ms.mulPose(Vector3f.YP.rotationDegrees(180f)); break;
                case NORTH:
                default: break;
            }

            ms.translate(0.0D, -7.6F / 16F, -1.8F / 16F);
            ms.scale(0.8F, 0.8F, 0.8F);
            ms.mulPose(Vector3f.XP.rotationDegrees(90F));

            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemCameraTransforms.TransformType.NONE, combinedLight, combinedOverlay, ms, buffer);

            ms.popPose();
        }

        ms.popPose();
    }
}
