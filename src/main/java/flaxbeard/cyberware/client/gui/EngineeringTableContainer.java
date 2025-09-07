package flaxbeard.cyberware.client.gui;

import flaxbeard.cyberware.common.block.entities.EngineeringTableBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class EngineeringTableContainer extends Container {
    private final EngineeringTableBlockEntity blockEntity;

    public EngineeringTableContainer(int windowId, PlayerInventory inv, EngineeringTableBlockEntity tile) {
        super(CyberwareContainers.ENGINEERING.get(), windowId);
        this.blockEntity = tile;

        addSlot(new EngineeringSlot(blockEntity.slots, 0, 15, 20));
        addSlot(new EngineeringSlot(blockEntity.slots, 1, 15, 53));
        addSlot(new EngineeringSlot(blockEntity.slots, 2, 71, 17));
        addSlot(new EngineeringSlot(blockEntity.slots, 3, 89, 17));
        addSlot(new EngineeringSlot(blockEntity.slots, 4, 71, 35));
        addSlot(new EngineeringSlot(blockEntity.slots, 5, 89, 35));
        addSlot(new EngineeringSlot(blockEntity.slots, 6, 71, 53));
        addSlot(new EngineeringSlot(blockEntity.slots, 7, 89, 53));
        addSlot(new EngineeringSlot(blockEntity.slots, 8, 115, 53));
        addSlot(new EngineeringSlot(blockEntity.slots, 9, 145, 21));

        int startX = 8;
        int startY = 84;

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(inv, col + row * 9 + 9,
                        startX + col * 18,
                        startY + row * 18));
            }
        }

        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(inv, col,
                    startX + col * 18,
                    startY + 58));
        }
    }

    @Override
    @Nonnull
    public ItemStack quickMoveStack(@Nonnull PlayerEntity player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            result = slotStack.copy();

            if (index < 10) {
                if (!this.moveItemStackTo(slotStack, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (blockEntity.slots.isItemValidForSlot(1, slotStack)) {
                    if (!this.moveItemStackTo(slotStack, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (blockEntity.slots.isItemValidForSlot(0, slotStack)) {
                    if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (blockEntity.slots.isItemValidForSlot(8, slotStack)) {
                    if (!this.moveItemStackTo(slotStack, 8, 9, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!this.moveItemStackTo(slotStack, 2, 8, false)) {
                        return ItemStack.EMPTY;
                    }
                }

                if (!slotStack.isEmpty()) {
                    if (index < 37) {
                        if (!this.moveItemStackTo(slotStack, 37, 46, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index < 46) {
                        if (!this.moveItemStackTo(slotStack, 10, 37, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, slotStack);
        }

        return result;
    }

    @Override
    public boolean stillValid(@Nonnull PlayerEntity player) {
        return this.blockEntity.stillValid(player);
    }

    public void notifyButtonClick(PlayerEntity player) {
        if (player == null || player.level.isClientSide) return;
        blockEntity.destruct(player);
    }

    public class EngineeringSlot extends SlotItemHandler {
        public EngineeringSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPickup(PlayerEntity player) {
            return true;
        }

        @Override
        public boolean mayPlace(@Nonnull ItemStack stack) {
            return blockEntity.slots.isItemValidForSlot(this.getSlotIndex(), stack);
        }
    }
}
