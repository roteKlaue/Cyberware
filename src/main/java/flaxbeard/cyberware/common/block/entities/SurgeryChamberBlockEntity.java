package flaxbeard.cyberware.common.block.entities;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;

public class SurgeryChamberBlockEntity extends TileEntity {
    public boolean lastOpen;
    public float openTicks;

    public SurgeryChamberBlockEntity() {
        super(CyberwareBlockEntities.SURGERY_CHAMBER.get());
    }

    @Override
    public void load(@Nonnull BlockState state,
                     @Nonnull CompoundNBT nbt) {
        super.load(state, nbt);
        this.lastOpen = nbt.getBoolean("LastOpen");
        this.openTicks = nbt.getFloat("OpenTicks");
    }

    @Override
    @Nonnull
    public CompoundNBT save(@Nonnull CompoundNBT nbt) {
        super.save(nbt);
        nbt.putBoolean("LastOpen", this.lastOpen);
        nbt.putFloat("OpenTicks", this.openTicks);
        return nbt;
    }
}
