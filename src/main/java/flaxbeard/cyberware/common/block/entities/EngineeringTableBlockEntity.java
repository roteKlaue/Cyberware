package flaxbeard.cyberware.common.block.entities;

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraftforge.items.ItemStackHandler;

public class EngineeringTableBlockEntity extends NameContainerProvider<EngineeringTableBlockEntity> implements ITickableTileEntity {
    private static final int SLOT_COUNT = 10;
    public final EngineeringTableItemStackHandler slots = new EngineeringTableItemStackHandler(this);

    public float clickedTime;

    public EngineeringTableBlockEntity() {
        super(CyberwareBlockEntities.ENGINEERING_TABLE.get(), "engineering_table",
                (a,b,c) -> null, EngineeringTableBlockEntity.class);
    }

    @Override
    public void tick() {
        if (level == null || level.isClientSide) return;

    }

     public static class EngineeringTableItemStackHandler extends ItemStackHandler {
        public EngineeringTableItemStackHandler(EngineeringTableBlockEntity engineeringTableBlockEntity) {
            super(SLOT_COUNT);
        }
    }
}
