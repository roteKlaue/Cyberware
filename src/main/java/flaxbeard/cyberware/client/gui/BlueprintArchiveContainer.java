package flaxbeard.cyberware.client.gui;


import flaxbeard.cyberware.common.block.entities.BlueprintArchiveBlockEntity;
import flaxbeard.cyberware.common.item.CyberwareItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class BlueprintArchiveContainer extends Container {
    private final BlueprintArchiveBlockEntity blockEntity;
    private final int numRows;

    public BlueprintArchiveContainer(int id, PlayerInventory playerInventory, BlueprintArchiveBlockEntity tileEntity) {
        super(CyberwareContainers.BLUEPRINT_ARCHIVE.get(), id);
        this.blockEntity = tileEntity;
        this.numRows = tileEntity.getContainerSize() / 9;

        buildSlots(playerInventory, tileEntity);
    }

    private static class BlueprintArchiveSlot extends Slot {
        public BlueprintArchiveSlot(IInventory inventory, int id, int x, int y) {
            super(inventory, id, x, y);
        }

        @Override
        public boolean mayPlace(@Nonnull ItemStack stack) {
            return CyberwareItems.COMPONENT.stream().anyMatch(r -> r.get() == stack.getItem());
        }
    }

    private void buildSlots(PlayerInventory playerInventory, IInventory backingInventory) {
        int yOffset = (numRows - 4) * 18;

        // component box's inventory
        for (int indexRow = 0; indexRow < numRows; indexRow++) {
            for (int indexColumn = 0; indexColumn < 9; indexColumn++) {
                int index = indexColumn + indexRow * 9;
                addSlot(new Slot(backingInventory, index, 8 + indexColumn * 18, 18 + indexRow * 18));
            }
        }

        // player's inventory
        for (int indexRow = 0; indexRow < 3; indexRow++) {
            for (int indexColumn = 0; indexColumn < 9; indexColumn++) {
                addSlot(new Slot(playerInventory, indexColumn + indexRow * 9 + 9, 8 + indexColumn * 18, 103 + indexRow * 18 + yOffset));
            }
        }

        // player's hotbar
        for (int indexColumn = 0; indexColumn < 9; indexColumn++) {
            addSlot(new Slot(playerInventory, indexColumn, 8 + indexColumn * 18, 161 + yOffset));
        }
    }

    @Override
    @Nonnull
    public ItemStack quickMoveStack(@Nonnull PlayerEntity player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            itemstack = stackInSlot.copy();

            int containerSlots = 18;

            if (index < containerSlots) {
                if (!this.moveItemStackTo(stackInSlot, containerSlots, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(stackInSlot, 0, containerSlots, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    public int getRowNumber() {
        return numRows;
    }

    @Override
    public boolean stillValid(@Nonnull PlayerEntity player) {
        return blockEntity.stillValid(player);
    }
}
