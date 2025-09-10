package flaxbeard.cyberware.common.block.entities;

import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.client.gui.ScannerContainer;
import flaxbeard.cyberware.common.CyberwareConfig;
import flaxbeard.cyberware.common.item.BlueprintItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;

import javax.annotation.Nonnull;

public class ScannerBlockEntity extends NameContainerProvider<ScannerBlockEntity> implements ITickableTileEntity {
    public static class ScannerItemStackHandler extends ItemStackHandler {
        private final ScannerBlockEntity entity;
        public ScannerItemStackHandler(ScannerBlockEntity entity, int size) {
            super(size);
            this.entity = entity;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (!isItemValidForSlot(slot, stack)) return stack;
            return super.insertItem(slot, stack, simulate);
        }

        public boolean isItemValidForSlot(int slot, ItemStack stack) {
            validateSlotIndex(slot);

            switch (slot) {
                case 0:
                    return CyberwareAPI.canDeconstruct(entity.level, stack);
                case 1:
                    return stack.getItem().equals(Items.PAPER);
                case 2:
                    return false;
            }
            return true;
        }
    }

    public final ScannerItemStackHandler slots = new ScannerItemStackHandler(this, 3);
    private final RangedWrapper slotsTopSides = new RangedWrapper(slots, 0, 2);
    private final RangedWrapper slotsBottom = new RangedWrapper(slots, 2, 3);
    private final RangedWrapper slotsBottom2 = new RangedWrapper(slots, 0, 1);

    private final LazyOptional<IItemHandlerModifiable> topCap = LazyOptional.of(() -> slotsTopSides);
    private final LazyOptional<IItemHandlerModifiable> bottomCap = LazyOptional.of(() -> {
        if (!slots.getStackInSlot(2).isEmpty() && !slots.getStackInSlot(0).isEmpty()) {
            return slotsBottom2;
        } else {
            return slotsBottom;
        }
    });

    public int ticks = 0;
    public int ticksMove = 0;
    public int lastX = 0;
    public int x = 0;
    public int lastZ = 0;
    public int z = 0;

    public ScannerBlockEntity() {
        super(CyberwareBlockEntities.SCANNER.get(), "scanner",
                ScannerContainer::new, ScannerBlockEntity.class);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side == Direction.DOWN) {
                return bottomCap.cast();
            } else {
                return topCap.cast();
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void load(@Nonnull BlockState state,
                     @Nonnull CompoundNBT tag) {
        super.load(state, tag);
        slots.deserializeNBT(tag.getCompound("inv"));
        ticks = tag.getInt("ticks");
    }

    @Override
    @Nonnull
    public CompoundNBT save(@Nonnull CompoundNBT tag) {
        super.save(tag);
        tag.put("inv", slots.serializeNBT());
        tag.putInt("ticks", ticks);
        return tag;
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT tag = new CompoundNBT();
        this.save(tag);
        return new SUpdateTileEntityPacket(this.worldPosition, 0, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.load(this.getBlockState(), pkt.getTag());
    }

    @Override
    @Nonnull
    public CompoundNBT getUpdateTag() {
        return this.save(new CompoundNBT());
    }

    public boolean isUsableByPlayer(PlayerEntity player) {
        if (this.level == null) return false;
        return this.level.getBlockEntity(this.worldPosition) == this &&
                player.distanceToSqr(this.worldPosition.getX() + 0.5D,
                        this.worldPosition.getY() + 0.5D,
                        this.worldPosition.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    public void tick() {
        ItemStack toDestroy = slots.getStackInSlot(0);
        if (CyberwareAPI.canDeconstruct(level, toDestroy)
                && toDestroy.getCount() > 0
                && slots.getStackInSlot(2).isEmpty()) {
            if (this.level == null) return;
            ticks++;

            if (ticksMove > ticks
                    || (ticks - ticksMove > Math.max(Math.abs(lastX - x) * 3, Math.abs(lastZ - z) * 3) + 10)) {
                ticksMove = ticks;
                lastX = x;
                lastZ = z;
                while (x == lastX) {
                    x = this.level.random.nextInt(11);
                }
                while (z == lastZ) {
                    z = this.level.random.nextInt(11);
                }
            }

            if (ticks > CyberwareConfig.SCANNER_TIME.get()) {
                ticks = 0;
                ticksMove = 0;

                if (!this.level.isClientSide && !slots.getStackInSlot(1).isEmpty()) {
                    float chance = calculateChance();

                    if (this.level.random.nextFloat() < (chance / 100F)) {
                        ItemStack stackBlueprint = BlueprintItem.makeBlueprint(level, toDestroy);
                        slots.setStackInSlot(2, stackBlueprint);
                        ItemStack current = slots.getStackInSlot(1);
                        current.shrink(1);
                        if (current.getCount() <= 0) {
                            current = ItemStack.EMPTY;
                        }
                        slots.setStackInSlot(1, current);
                        this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 2);
                    }
                }
            }
            this.setChanged();
        } else {
            x = lastX = z = lastZ = 0;
            if (ticks != 0) {
                ticks = 0;
                this.setChanged();
            }
        }
    }

    public float getProgress() {
        return (ticks * 1F) / CyberwareConfig.SCANNER_TIME.get();
    }

    public float calculateChance() {
        float chance = 0F;
        if (!slots.getStackInSlot(0).isEmpty()) {
            chance = ((float)(double) CyberwareConfig.SCANNER_CHANCE.get()) +
                    (float) (CyberwareConfig.SCANNER_CHANCE_ADDL.get() * (slots.getStackInSlot(0).getCount() - 1));

            if (slots.getStackInSlot(0).isDamageableItem()) {
                chance = 50F * (1F - (slots.getStackInSlot(0).getDamageValue() * 1F / slots.getStackInSlot(0).getMaxDamage()));
            }

            chance = Math.min(chance, 50F);
        }
        return chance;
    }
}
