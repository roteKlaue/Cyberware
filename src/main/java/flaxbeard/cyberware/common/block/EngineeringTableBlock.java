package flaxbeard.cyberware.common.block;

import flaxbeard.cyberware.common.block.entities.EngineeringTableBlockEntity;
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
