package flaxbeard.cyberware.common.block;

import flaxbeard.cyberware.common.block.entities.EngineeringTableBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EngineeringTableBlock extends TallBlock<EngineeringTableBlockEntity> {
    private static final VoxelShape TOP_SOUTH = VoxelShapes.or(
            Block.box(4, 8, 4, 12, 16, 12),
            Block.box(4, 0, 0, 12, 16, 4)
    );

    private static final VoxelShape TOP_WEST = VoxelShapes.or(
            Block.box(4, 8, 4, 12, 16, 12),
            Block.box(12, 0, 4, 16, 16, 12)
    );

    private static final VoxelShape TOP_NORTH = VoxelShapes.or(
            Block.box(4, 8, 4, 12, 16, 12),
            Block.box(4, 0, 12, 12, 16, 16)
    );

    private static final VoxelShape TOP_EAST = VoxelShapes.or(
            Block.box(4, 8, 4, 12, 16, 12),
            Block.box(0, 0, 4, 4, 16, 12)
    );

    public EngineeringTableBlock() {
        super(Properties.of(Material.WOOD), EngineeringTableBlockEntity::new, EngineeringTableBlockEntity.class);
    }

    @Override
    public void setPlacedBy(@Nonnull World world,
                            @Nonnull BlockPos pos,
                            @Nonnull BlockState state,
                            @Nullable LivingEntity placer,
                            @Nonnull ItemStack stack) {
        if (stack.hasCustomHoverName()) {
            TileEntity tile = world.getBlockEntity(pos);
            if (tile instanceof EngineeringTableBlockEntity) {
                ((EngineeringTableBlockEntity) tile).setCustomName(stack.getHoverName());
            }
        }
        super.setPlacedBy(world, pos, state, placer, stack);
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public ItemStack getCloneItemStack(IBlockReader world, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        TileEntity tile = world.getBlockEntity(pos);
        if (tile instanceof EngineeringTableBlockEntity) {
            ItemStack stack = new ItemStack(this);
            ITextComponent name = ((EngineeringTableBlockEntity) tile).getDisplayName();
            if (tile instanceof EngineeringTableBlockEntity && !(name instanceof TranslationTextComponent)) {
                stack.setHoverName(name);
            }
            return stack;
        }
        return super.getCloneItemStack(world, pos, state);
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public ActionResultType use(@Nonnull BlockState state,
                                @Nonnull World world,
                                @Nonnull BlockPos position,
                                @Nonnull PlayerEntity player,
                                @Nonnull Hand hand,
                                @Nonnull BlockRayTraceResult result) {
        if (world.isClientSide) return ActionResultType.PASS;

        BlockPos pos = isTop(state) ? position
                : position.above();
        TileEntity tile = world.getBlockEntity(pos);
        if (tile instanceof EngineeringTableBlockEntity) {
            EngineeringTableBlockEntity provider = (EngineeringTableBlockEntity) tile;
            NetworkHooks.openGui((ServerPlayerEntity) player, provider, pos);
            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(@Nonnull BlockState state,
                               @Nonnull IBlockReader worldIn,
                               @Nonnull BlockPos pos,
                               @Nonnull ISelectionContext context) {
        Direction facing = state.getValue(FACING);
        if (!isTop(state)) return VoxelShapes.block();
        return getDirectionalShape(facing, TOP_EAST, TOP_SOUTH, TOP_WEST, TOP_NORTH);
    }
}
