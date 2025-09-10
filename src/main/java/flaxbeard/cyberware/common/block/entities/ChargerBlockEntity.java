package flaxbeard.cyberware.common.block.entities;

import lombok.Getter;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ChargerBlockEntity extends TileEntity implements ITickableTileEntity, IEnergyStorage {
    private final PowerHandler handler = new PowerHandler(5000, 50, 50);
    private final LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> this);
    private boolean last = false;

    public ChargerBlockEntity() {
        super(CyberwareBlockEntities.CHARGER.get());
    }

    @Override
    public void load(@Nonnull BlockState blockState, @Nonnull CompoundNBT compoundNBTd) {
        super.load(blockState, compoundNBTd);
        handler.deserializeNBT(compoundNBTd.getCompound("power"));
    }

    @Override
    @Nonnull
    public CompoundNBT save(@Nonnull CompoundNBT tag) {
        super.save(tag);
        tag.put("power", handler.serializeNBT());
        return tag;
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.load(this.getBlockState(), pkt.getTag());
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT tag = new CompoundNBT();
        this.save(tag);
        return new SUpdateTileEntityPacket(this.worldPosition, 0, tag);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY) {
            return energyCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        energyCap.invalidate();
    }
    @Override
    public void tick() {
        if (level == null || level.isClientSide) return;

        List<LivingEntity> entities = level.getEntitiesOfClass(
                LivingEntity.class,
                new AxisAlignedBB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                        worldPosition.getX() + 1, worldPosition.getY() + 2.5, worldPosition.getZ() + 1)
        );

        // TODO: finish charging logic
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return (int) handler.addPower(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return (int) handler.removePower(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return (int) handler.getStored();
    }

    @Override
    public int getMaxEnergyStored() {
        return (int) handler.getCapacity();
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
    }


    @Getter
    public static class PowerHandler implements INBTSerializable<CompoundNBT> {
        private long stored;
        private long capacity;
        private long inputRate;
        private long outputRate;

        public PowerHandler(long capacity, long inputRate, long outputRate) {
            this.capacity = capacity;
            this.inputRate = inputRate;
            this.outputRate = outputRate;
            this.stored = 0;
        }

        @Override
        public CompoundNBT serializeNBT() {
            CompoundNBT nbt = new CompoundNBT();
            nbt.putLong("power", stored);
            nbt.putLong("capacity", capacity);
            nbt.putLong("input", inputRate);
            nbt.putLong("output", outputRate);
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            stored = nbt.getLong("power");
            capacity = nbt.getLong("capacity");
            inputRate = nbt.getLong("input");
            outputRate = nbt.getLong("output");
            stored = Math.min(stored, capacity);
        }

        public long addPower(long tesla, boolean simulated) {
            long acceptedTesla = Math.min(getCapacity() - stored, Math.min(inputRate, tesla));
            if (!simulated) stored += acceptedTesla;
            return acceptedTesla;
        }

        public long removePower(long tesla, boolean simulated) {
            long removedPower = Math.min(stored, Math.min(outputRate, tesla));
            if (!simulated) stored -= removedPower;
            return removedPower;
        }
    }
}
