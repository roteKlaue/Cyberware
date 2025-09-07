package flaxbeard.cyberware.common.block;

import flaxbeard.cyberware.common.block.entities.CyberwareBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class SurgeryBlock extends Block {
    public SurgeryBlock() {
        super(Properties.copy(Blocks.IRON_BLOCK));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return CyberwareBlockEntities.SURGERY.get().create();
    }
}
