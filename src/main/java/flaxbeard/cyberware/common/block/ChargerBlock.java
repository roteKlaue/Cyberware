package flaxbeard.cyberware.common.block;

import flaxbeard.cyberware.common.block.entities.ChargerBlockEntity;
import flaxbeard.cyberware.common.block.entities.CyberwareBlockEntities;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class ChargerBlock extends Block {
    public ChargerBlock() {
        super(Properties.copy(Blocks.IRON_BLOCK));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public ChargerBlockEntity createTileEntity(BlockState state, IBlockReader world) {
        return CyberwareBlockEntities.CHARGER.get().create();
    }
}
