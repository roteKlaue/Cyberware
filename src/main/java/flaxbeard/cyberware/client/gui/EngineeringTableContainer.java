package flaxbeard.cyberware.client.gui;

import flaxbeard.cyberware.common.block.entities.BlueprintArchiveBlockEntity;
import flaxbeard.cyberware.common.block.entities.ComponentBoxBlockEntity;
import flaxbeard.cyberware.common.block.entities.EngineeringTableBlockEntity;

import lombok.Getter;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class EngineeringTableContainer extends Container {
    @Getter
    private final EngineeringTableBlockEntity blockEntity;
    private final List<Slot> dynamicSlots = new ArrayList<>();

    public BlueprintArchiveBlockEntity archive;
    public int archiveIndex = 0;
    public ArrayList<BlueprintArchiveBlockEntity> archiveList = new ArrayList<>();

    public ComponentBoxBlockEntity componentBox;
    public ArrayList<ComponentBoxBlockEntity> componentBoxList = new ArrayList<>();
    public int componentBoxIndex = 0;

    public EngineeringTableContainer(int windowId, PlayerInventory inv, EngineeringTableBlockEntity blockEntity) {
        super(CyberwareContainers.ENGINEERING.get(), windowId);
        this.blockEntity = blockEntity;

        addSlot(new EngineeringSlot(this.blockEntity.slots, 0, 15, 20));
        addSlot(new EngineeringSlot(this.blockEntity.slots, 1, 15, 53));
        addSlot(new EngineeringSlot(this.blockEntity.slots, 2, 71, 17));
        addSlot(new EngineeringSlot(this.blockEntity.slots, 3, 89, 17));
        addSlot(new EngineeringSlot(this.blockEntity.slots, 4, 71, 35));
        addSlot(new EngineeringSlot(this.blockEntity.slots, 5, 89, 35));
        addSlot(new EngineeringSlot(this.blockEntity.slots, 6, 71, 53));
        addSlot(new EngineeringSlot(this.blockEntity.slots, 7, 89, 53));
        addSlot(new EngineeringSlot(this.blockEntity.slots, 8, 115, 53));
        addSlot(new EngineeringOutputSlot(this.blockEntity, 9, 145, 21));

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

        archive = null;
        componentBox = null;
        BlockPos target = null;

        String uuid = inv.player.getStringUUID();
        if (blockEntity.lastPlayerArchive.containsKey(uuid)) {
            target = blockEntity.lastPlayerArchive.get(uuid);
        }

        for (int y = -2; y < 2; y++) {
            for (int x = -2; x < 3; x++) {
                for (int z = -2; z < 3; z++) {
                    BlockPos pos = blockEntity.getBlockPos().offset(x, y, z);
                    TileEntity tileEntity = null;
                    if (blockEntity.getLevel() != null) {
                        tileEntity = blockEntity.getLevel().getBlockEntity(pos);
                    }
                    if (tileEntity instanceof BlueprintArchiveBlockEntity) {
                        if (archive == null || tileEntity.getBlockPos().equals(target)) {
                            archive = (BlueprintArchiveBlockEntity) tileEntity;
                            archiveIndex = archiveList.size();
                        }

                        archiveList.add((BlueprintArchiveBlockEntity) tileEntity);
                    }
                }
            }
        }

        for (int y = -2; y < 2; y++) {
            for (int x = -2; x < 3; x++) {
                for (int z = -2; z < 3; z++) {
                    BlockPos pos = blockEntity.getBlockPos().offset(x, y, z);
                    TileEntity tileEntity = blockEntity.getLevel().getBlockEntity(pos);
                    if (tileEntity instanceof ComponentBoxBlockEntity) {
                        if (componentBox == null) {
                            componentBox = (ComponentBoxBlockEntity) tileEntity;
                            componentBoxIndex = componentBoxList.size();
                        }
                        componentBoxList.add((ComponentBoxBlockEntity) tileEntity);
                    }
                }
            }
        }

        rebuildDynamicSlots();
    }

    @Override
    @Nonnull
    public ItemStack quickMoveStack(@Nonnull PlayerEntity player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            result = slotStack.copy();

            if (index == 9) { // TODO: implement multi crafting
                if (player.level.isClientSide) return ItemStack.EMPTY;

                ItemStack outputStack = slot.getItem();
                if (outputStack.isEmpty()) return ItemStack.EMPTY;

                ItemStack retStack = outputStack.copy();
                retStack.setCount(1);

                if (!this.moveItemStackTo(retStack, 10, 46, false)) {
                    return ItemStack.EMPTY;
                }

                blockEntity.extractCrafts(1);

                outputStack.shrink(1);
                if (outputStack.getCount() <= 0) {
                    slot.set(ItemStack.EMPTY);
                } else {
                    slot.setChanged();
                }

                blockEntity.refreshCraftingResult();
                this.broadcastChanges();
                slot.onTake(player, retStack);
                return retStack;
            }

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
        blockEntity.destruct();
    }

    public void prevArchive() {
        if (archiveList.isEmpty()) return;

        archiveIndex = (archiveIndex + 1) % archiveList.size();
        archive = archiveList.get(archiveIndex);

        rebuildDynamicSlots();
    }

    public void nextArchive() {
        if (archiveList.isEmpty()) return;

        archiveIndex = (archiveIndex - 1 + archiveList.size()) % archiveList.size();
        archive = archiveList.get(archiveIndex);

        rebuildDynamicSlots();
    }

    public void nextComponentBox() {
        if (componentBoxList.isEmpty()) return;

        componentBoxIndex = (componentBoxIndex + 1) % componentBoxList.size();
        componentBox = componentBoxList.get(componentBoxIndex);

        rebuildDynamicSlots();
    }

    public void prevComponentBox() {
        if (componentBoxList.isEmpty()) return;

        componentBoxIndex = (componentBoxIndex - 1 + componentBoxList.size()) % componentBoxList.size();
        componentBox = componentBoxList.get(componentBoxIndex);

        rebuildDynamicSlots();
    }

    private void rebuildDynamicSlots() {
        for (Slot slot : dynamicSlots) {
            this.slots.remove(slot);
        }
        dynamicSlots.clear();

        if (archive != null) {
            IInventory inventory = archive;


            final int rows = 6;
            final int cols = (int) Math.ceil(inventory.getContainerSize() / (double) rows);

            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    int index = col + row * cols;

                    if (index >= inventory.getContainerSize()) {
                        continue;
                    }

                    Slot slot = new BlueprintArchiveContainer.BlueprintArchiveSlot(
                            inventory,
                            index,
                            181 + col * 18,
                            22 + row * 18
                    );

                    dynamicSlots.add(slot);
                    addSlot(slot);
                }
            }
        }

        if (componentBox instanceof ComponentBoxBlockEntity) {
            IInventory inventory = componentBox;


            final int rows = 6;
            final int cols = (int) Math.ceil(inventory.getContainerSize() / (double) rows);

            for (int row = 0; row < 6; row++) {
                for (int col = 0; col < cols; col++) {
                    int index = col + row * cols;
                    Slot s = new ComponentBoxContainer.ComponentBoxSlot(
                            inventory,
                            index,
                            -56 + col * 18,
                            22 + row * 18
                    );
                    dynamicSlots.add(s);
                    this.addSlot(s);
                }
            }
        }
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

    public class EngineeringOutputSlot extends EngineeringSlot {
        private final EngineeringTableBlockEntity entity;
        public EngineeringOutputSlot(EngineeringTableBlockEntity entity, int index, int xPosition, int yPosition) {
            super(entity.slots, index, xPosition, yPosition);
            this.entity = entity;
        }

        @Override
        public void setChanged() {
            super.setChanged();
            blockEntity.refreshCraftingResult();
        }

        @Override
        @Nonnull
        public ItemStack onTake(PlayerEntity player, @Nonnull ItemStack stack) {
            if (player.level.isClientSide) return stack;

            int amountTaken = stack.getCount();
            for (int i = 0; i < amountTaken; i++) {
                entity.extractCrafts(1);
            }

            entity.refreshCraftingResult();
            return super.onTake(player, stack);
        }
    }

    public static EngineeringTableContainer of(int windowId, PlayerInventory inv, PacketBuffer data) {
        BlockPos pos = data.readBlockPos();
        TileEntity tile = inv.player.level.getBlockEntity(pos);
        if (tile instanceof EngineeringTableBlockEntity) {
            return new EngineeringTableContainer(windowId, inv, (EngineeringTableBlockEntity) tile);
        }
        return null;
    }
}
