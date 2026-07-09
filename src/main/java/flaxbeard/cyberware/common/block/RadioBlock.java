package flaxbeard.cyberware.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;

public class RadioBlock extends DirectionalBlock {
    private static final VoxelShape SOUTH_SHAPE = Block.box(1.0, 0.0, 2.0, 13.0, 4.0, 10.0);
    private static final VoxelShape NORTH_SHAPE = Block.box(3.0, 0.0, 6.0, 15.0, 4.0, 14.0);
    private static final VoxelShape EAST_SHAPE = Block.box(2.0, 0.0, 3.0, 10.0, 4.0, 15.0);
    private static final VoxelShape WEST_SHAPE = Block.box(6.0, 0.0, 1.0, 14.0, 4.0, 13.0);

    public RadioBlock() {
        super(Properties.of(Material.METAL)
                .strength(5.0F)
        );
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(@Nonnull BlockState state,
                               @Nonnull IBlockReader worldIn,
                               @Nonnull BlockPos pos,
                               @Nonnull ISelectionContext context) {
        return getDirectionalShape(state, SOUTH_SHAPE, WEST_SHAPE, NORTH_SHAPE, EAST_SHAPE);
    }
}
