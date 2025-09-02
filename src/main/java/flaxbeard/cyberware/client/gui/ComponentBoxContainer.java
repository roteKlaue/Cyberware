package flaxbeard.cyberware.client.gui;

import flaxbeard.cyberware.common.block.entities.ComponentBoxBlockEntity;
import flaxbeard.cyberware.common.block.items.ComponentBoxItem;
import flaxbeard.cyberware.common.item.CyberwareItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;

public class ComponentBoxContainer extends Container {
    private final ComponentBoxBlockEntity tileEntity;
    private final NonNullList<ItemStack> items;
    private final int numRows;

    private static class ComponentBoxSlot extends Slot {
        public ComponentBoxSlot(IInventory inventory, int id, int x, int y) {
            super(inventory, id, x, y);
        }

        @Override
        public boolean mayPlace(@Nonnull ItemStack stack) {
            return CyberwareItems.COMPONENT.stream().anyMatch(r -> r.get() == stack.getItem());
        }
    }

    public ComponentBoxContainer(int id, PlayerInventory playerInventory, CompoundNBT beTag) {
        super(CyberwareContainers.COMPONENT_BOX.get(), id);

        this.tileEntity = null;
        if (beTag == null) beTag = new CompoundNBT();

        this.items = NonNullList.withSize(18, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(beTag, this.items);

        this.numRows = this.items.size() / 9;

        buildSlots(playerInventory, new SimpleInventoryWrapper(this.items));
    }

    public ComponentBoxContainer(int id, PlayerInventory playerInventory, PacketBuffer data) {
        super(CyberwareContainers.COMPONENT_BOX.get(), id);

        ComponentBoxBlockEntity be = null;
        NonNullList<ItemStack> tmpItems = null;

        try {
            if (data.readableBytes() == 8) {
                be = (ComponentBoxBlockEntity) playerInventory.player.level.getBlockEntity(data.readBlockPos());
            } else {
                CompoundNBT beTag = data.readNbt();
                if (beTag == null) beTag = new CompoundNBT();
                tmpItems = NonNullList.withSize(18, ItemStack.EMPTY);
                ItemStackHelper.loadAllItems(beTag, tmpItems);
            }
        } catch (Exception ignored) {}

        this.tileEntity = be;
        this.items = tmpItems;
        if ((this.tileEntity != null)) {
            this.numRows = this.tileEntity.getContainerSize() / 9;
        } else {
            assert this.items != null;
            this.numRows = (this.items.size() / 9);
        }

        // Build slots using an IInventory wrapper that points to either the tile entity or our tmpItems
        if (this.tileEntity != null) {
            buildSlots(playerInventory, this.tileEntity);
        } else {
            // Create a small IInventory wrapper around the NonNullList for Slot use
            buildSlots(playerInventory, new SimpleInventoryWrapper(this.items));
        }
    }

    public ComponentBoxContainer(int id, PlayerInventory playerInventory, ComponentBoxBlockEntity tileEntity) {
        super(CyberwareContainers.COMPONENT_BOX.get(), id);
        this.tileEntity = tileEntity;
        this.numRows = tileEntity.getContainerSize() / 9;
        this.items = NonNullList.withSize(this.numRows, ItemStack.EMPTY);

        buildSlots(playerInventory, this.tileEntity);
    }

    private void buildSlots(PlayerInventory playerInventory, IInventory backingInventory) {
        int yOffset = (numRows - 4) * 18;

        // component box's inventory
        for (int indexRow = 0; indexRow < numRows; indexRow++) {
            for (int indexColumn = 0; indexColumn < 9; indexColumn++) {
                int index = indexColumn + indexRow * 9;
                addSlot(new ComponentBoxSlot(backingInventory, index, 8 + indexColumn * 18, 18 + indexRow * 18));
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
    public boolean stillValid(@Nonnull PlayerEntity player) {
        if (tileEntity != null) {
            return tileEntity.stillValid(player);
        }
        return true;
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
    public void removed(@Nonnull PlayerEntity player) {
        super.removed(player);

        if (this.tileEntity == null && this.items != null) {
            ItemStack held = player.getMainHandItem();

            if (held.getItem() instanceof ComponentBoxItem) {
                CompoundNBT tag = held.getOrCreateTag();
                CompoundNBT beTag = new CompoundNBT();
                ItemStackHelper.saveAllItems(beTag, this.items);
                tag.put("BlockEntityTag", beTag);
            }
        }
    }

    private static class SimpleInventoryWrapper implements IInventory {
        private final NonNullList<ItemStack> list;

        SimpleInventoryWrapper(NonNullList<ItemStack> list) {
            this.list = list;
        }

        @Override public int getContainerSize() { return list.size(); }
        @Override public boolean isEmpty() { return list.stream().allMatch(ItemStack::isEmpty); }
        @Override @Nonnull public ItemStack getItem(int index) { return list.get(index); }
        @Override @Nonnull public ItemStack removeItem(int index, int count) { return ItemStackHelper.removeItem(list, index, count); }
        @Override @Nonnull public ItemStack removeItemNoUpdate(int index) { return ItemStackHelper.takeItem(list, index); }
        @Override public void setItem(int index, @Nonnull ItemStack stack) { list.set(index, stack); }
        @Override public void setChanged() {}
        @Override public boolean stillValid(@Nonnull PlayerEntity player) { return true; }
        @Override public void clearContent() { list.clear(); }
        @Override public void startOpen(@Nonnull PlayerEntity player) { }
        @Override public void stopOpen(@Nonnull PlayerEntity player) { }
    }
}
