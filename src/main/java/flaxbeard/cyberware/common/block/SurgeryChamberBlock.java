package flaxbeard.cyberware.common.block;

import flaxbeard.cyberware.common.block.entities.SurgeryBlockEntity;
import flaxbeard.cyberware.common.block.entities.SurgeryChamberBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class SurgeryChamberBlock extends TallBlock<SurgeryChamberBlockEntity> {
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

    public static final VoxelShape BOTTOM_SOUTH = VoxelShapes.or(
            Block.box(2, 1, 0, 14, 16, 2),
            Block.box(0, 1, 0, 2, 16, 16),
            Block.box(14, 1, 0, 16, 16, 16),
            Block.box(0, 0, 0, 16, 1, 16)
    );

    public static final VoxelShape BOTTOM_WEST = VoxelShapes.or(
            Block.box(14, 1, 2, 16, 16, 14),
            Block.box(0, 1, 14, 16, 16, 16),
            Block.box(0, 1, 0, 16, 16, 2),
            Block.box(0, 0, 0, 16, 1, 16)
    );

    public static final VoxelShape BOTTOM_NORTH = VoxelShapes.or(
            Block.box(2, 1, 14, 14, 16, 16),
            Block.box(14, 1, 0, 16, 16, 16),
            Block.box(0, 1, 0, 2, 16, 16),
            Block.box(0, 0, 0, 16, 1, 16)
    );

    public static final VoxelShape BOTTOM_EAST = VoxelShapes.or(
            Block.box(0, 1, 2, 2, 16, 14),
            Block.box(0, 1, 0, 16, 16, 2),
            Block.box(0, 1, 14, 16, 16, 16),
            Block.box(0, 0, 0, 16, 1, 16)
    );

    public static final VoxelShape TOP_SOUTH = VoxelShapes.or(
            Block.box(2, 0, 0, 14, 15, 2),
            Block.box(0, 0, 0, 2, 15, 16),
            Block.box(14, 0, 0, 16, 15, 16),
            Block.box(0, 15, 0, 16, 16, 16)
    );

    public static final VoxelShape TOP_WEST = VoxelShapes.or(
            Block.box(14, 0, 2, 16, 15, 14),
            Block.box(0, 0, 14, 16, 15, 16),
            Block.box(0, 0, 0, 16, 15, 2),
            Block.box(0, 15, 0, 16, 16, 16)
    );

    public static final VoxelShape TOP_NORTH = VoxelShapes.or(
            Block.box(2, 0, 14, 14, 15, 16),
            Block.box(14, 0, 0, 16, 15, 16),
            Block.box(0, 0, 0, 2, 15, 16),
            Block.box(0, 15, 0, 16, 16, 16)
    );

    public static final VoxelShape TOP_EAST = VoxelShapes.or(
            Block.box(0, 0, 2, 2, 15, 14),
            Block.box(0, 0, 0, 16, 15, 2),
            Block.box(0, 0, 14, 16, 15, 16),
            Block.box(0, 15, 0, 16, 16, 16)
    );

    public static final VoxelShape TOP_CLOSED = VoxelShapes.or(
            Block.box(0, 0, 0, 16, 16, 2),
            Block.box(0, 0, 14, 16, 16, 16),
            Block.box(0, 0, 2, 2, 16, 14),
            Block.box(14, 0, 2, 16, 16, 14),
            Block.box(2, 15, 2, 14, 16, 14)
    );

    public static final VoxelShape BOTTOM_CLOSED = VoxelShapes.or(
            Block.box(0, 0, 0, 16, 16, 2),
            Block.box(0, 0, 14, 16, 16, 16),
            Block.box(0, 0, 2, 2, 16, 14),
            Block.box(14, 0, 2, 16, 16, 14),
            Block.box(2, 0, 2, 14, 1, 14)
    );

    public SurgeryChamberBlock() {
        super(Properties.of(Material.METAL), SurgeryChamberBlockEntity::new, SurgeryChamberBlockEntity.class);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(OPEN, false));
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public ActionResultType use(@Nonnull BlockState state, World world, @Nonnull BlockPos pos,
                                @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult hit) {
        if (!world.isClientSide) {
            if (canOpen(world, pos, state)) {
                toggleDoor(world, pos, state);

                SurgeryBlockEntity te = getSurgeryBlockEntity(world, pos, isTop(state));
                if (te != null) {
                    te.notifyChange();
                }
            }
        }
        return ActionResultType.sidedSuccess(world.isClientSide);
    }

    public void toggleDoor(World world, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        boolean newOpen = !state.getValue(OPEN);
        toggleDoor(world, pos, state, newOpen);
    }

    public void toggleDoor(World world, @Nonnull BlockPos pos, @Nonnull BlockState state, boolean newOpen) {
        BlockState newState = state.setValue(OPEN, newOpen);
        world.setBlock(pos, newState, 2);

        BlockPos otherPos = isTop(state) ? pos.below() : pos.above();
        BlockState other = world.getBlockState(otherPos);
        if (other.getBlock() == this) {
            world.setBlock(otherPos, other.setValue(OPEN, newOpen), 2);
        }
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        if (context.getEntity() != null) return VoxelShapes.block();
        return getCollisionShape(state, worldIn, pos, context);
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public  VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        boolean open = state.getValue(OPEN);
        if (isTop(state)) {
            return !open ? TOP_CLOSED :
                    getDirectionalShape(state, TOP_SOUTH, TOP_WEST, TOP_NORTH, TOP_EAST);
        } else {
            return !open ? BOTTOM_CLOSED :
                    getDirectionalShape(state, BOTTOM_SOUTH, BOTTOM_WEST, BOTTOM_NORTH, BOTTOM_EAST);
        }
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public PushReaction getPistonPushReaction(@Nonnull BlockState state) {
        return PushReaction.DESTROY;
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(OPEN);
    }

    private boolean canOpen(World world, @Nonnull BlockPos pos, @Nonnull BlockState state) {
        SurgeryBlockEntity te = getSurgeryBlockEntity(world, pos, isTop(state));
        return te == null || te.canOpen();
    }

    private SurgeryBlockEntity getSurgeryBlockEntity(World world, @Nonnull BlockPos pos, boolean top) {
        pos = pos.above();
        if (!top) pos = pos.above();

        TileEntity te = world.getBlockEntity(pos);
        if (!(te instanceof SurgeryBlockEntity)) return null;

        return (SurgeryBlockEntity) te;
    }
}
