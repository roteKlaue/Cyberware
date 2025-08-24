package flaxbeard.cyberware.common.block;

import flaxbeard.cyberware.common.block.entities.SurgeryChamberBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;

public class SurgeryChamberBlock extends TallBlock<SurgeryChamberBlockEntity> {
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

    public SurgeryChamberBlock() {
        super(Properties.of(Material.METAL), SurgeryChamberBlockEntity::new, SurgeryChamberBlockEntity.class);
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(@Nonnull BlockState state,
                               @Nonnull IBlockReader worldIn,
                               @Nonnull BlockPos pos,
                               @Nonnull ISelectionContext context) {
        Direction facing = state.getValue(FACING);
        if (isBottom(state)) {
            return getVoxelShape(facing, BOTTOM_EAST, BOTTOM_SOUTH, BOTTOM_WEST, BOTTOM_NORTH);
        }
        return getVoxelShape(facing, TOP_EAST, TOP_SOUTH, TOP_WEST, TOP_NORTH);
    }
}
