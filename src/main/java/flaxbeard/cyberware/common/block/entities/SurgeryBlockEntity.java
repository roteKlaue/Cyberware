package flaxbeard.cyberware.common.block.entities;

import flaxbeard.cyberware.common.block.SurgeryChamberBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class SurgeryBlockEntity extends TileEntity implements ITickableTileEntity {
    public LivingEntity targetEntity;
    public int progressTicks;
    public boolean inProgress = false;
    public int cooldownTicks = 0;

    public SurgeryBlockEntity() {
        super(CyberwareBlockEntities.SURGERY.get());
    }

    public boolean canOpen() {
        return !inProgress && cooldownTicks <= 0;
    }

    public void notifyChange() {
        if (level == null) return;

        BlockPos chamberPos = worldPosition.below();
        BlockState state = level.getBlockState(chamberPos);
        if (!(state.getBlock() instanceof SurgeryChamberBlock)) return;
        if (state.getValue(SurgeryChamberBlock.OPEN)) return;

        List<LivingEntity> entities = level.getEntitiesOfClass(
                LivingEntity.class,
                new AxisAlignedBB(
                        chamberPos.getX(), chamberPos.getY(), chamberPos.getZ(),
                        chamberPos.getX() + 1, chamberPos.getY() + 2, chamberPos.getZ() + 1
                )
        );
        if (entities.size() != 1) return;
        LivingEntity entity = entities.get(0);
        // CyberwareSurgeryEvent.Pre preSurgeryEvent = new CyberwareSurgeryEvent.Pre(entityLivingBase, slotsPlayer, slots);

        if (true /*!MinecraftForge.EVENT_BUS.post(preSurgeryEvent)*/)
        {
            this.inProgress = true;
            this.progressTicks = 0;
            this.targetEntity = entity;
        }
        else
        {
            ((SurgeryChamberBlock) state.getBlock())
                    .toggleDoor(level, chamberPos, state);
        }
    }

    @Override
    public void tick() {
        if (inProgress) {
            progressTicks++;

            if (progressTicks >= 80) {
                inProgress = false;
                progressTicks = 0;
                targetEntity = null;
                cooldownTicks = 60;
            }
        }

        cooldownTicks = Math.max(--cooldownTicks, 0);
    }
}
