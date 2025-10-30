package flaxbeard.cyberware.common.block.entities;

import net.minecraft.tileentity.TileEntity;

public class SurgeryBlockEntity extends TileEntity {
    public boolean inProgress = false;
    public int cooldownTicks = 0;

    public SurgeryBlockEntity() {
        super(CyberwareBlockEntities.SURGERY.get());
    }

    public boolean canOpen() {
        return !inProgress && cooldownTicks <= 0;
    }

    public void notifyChange() {
    }
}
