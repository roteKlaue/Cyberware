package flaxbeard.cyberware.common.block;

import flaxbeard.cyberware.common.block.entities.CyberwareBlockEntities;
import flaxbeard.cyberware.common.block.entities.ScannerBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nonnull;

public class ScannerBlock extends NamedContainerBlock<ScannerBlockEntity> {
    private static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 15, 16);

    public ScannerBlock() {
        super(Properties.copy(Blocks.IRON_BLOCK),
                CyberwareBlockEntities.SCANNER,
                ScannerBlockEntity.class,
                new NamedContainerProviderAccessor<>());
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(@Nonnull BlockState state,
                               @Nonnull IBlockReader worldIn,
                               @Nonnull BlockPos pos,
                               @Nonnull ISelectionContext context) {
        return SHAPE;
    }
}
