package flaxbeard.cyberware.client.gui;

import flaxbeard.cyberware.common.block.entities.ScannerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class ScannerContainer extends Container {
    public final ScannerBlockEntity scanner;

    public ScannerContainer(int id, PlayerInventory playerInventory, ScannerBlockEntity tileEntity) {
        super(CyberwareContainers.SCANNER.get(), id);
        this.scanner = tileEntity;

        // scanner slots
        addSlot(new SlotScanner(scanner.slots, 0, 35, 53));
        addSlot(new SlotScanner(scanner.slots, 1, 15, 53));
        addSlot(new SlotScanner(scanner.slots, 2, 141, 57));

        // player inventory (3 rows x 9)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        // hotbar
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public boolean stillValid(@Nonnull PlayerEntity player) {
        return scanner.isUsableByPlayer(player);
    }

    @Nonnull
    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            result = slotStack.copy();

            if (index == 2) {
                if (!this.moveItemStackTo(slotStack, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
            }

            else if (index > 2) {
                if (scanner.slots.isItemValidForSlot(1, slotStack)) {
                    if (!this.moveItemStackTo(slotStack, 1, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (scanner.slots.isItemValidForSlot(0, slotStack)) {
                    if (!this.moveItemStackTo(slotStack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 30) { // move from main to hotbar
                    if (!this.moveItemStackTo(slotStack, 30, 39, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 39) { // move from hotbar to main
                    if (!this.moveItemStackTo(slotStack, 3, 30, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            else if (!this.moveItemStackTo(slotStack, 3, 39, false)) {
                return ItemStack.EMPTY;
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

    public class SlotScanner extends SlotItemHandler {
        public SlotScanner(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPickup(PlayerEntity player) {
            return true;
        }

        @Override
        public boolean mayPlace(@Nonnull ItemStack stack) {
            return scanner.slots.isItemValidForSlot(this.getSlotIndex(), stack);
        }
    }
}
