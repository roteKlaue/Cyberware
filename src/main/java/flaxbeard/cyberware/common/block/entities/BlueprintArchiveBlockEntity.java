package flaxbeard.cyberware.common.block.entities;

import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.client.gui.BlueprintArchiveContainer;
import flaxbeard.cyberware.common.item.CyberwareItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import java.util.Collections;

public class BlueprintArchiveBlockEntity extends LockableTileEntity {
    private static final int SIZE = 18;
    private final NonNullList<ItemStack> items = NonNullList.withSize(SIZE, ItemStack.EMPTY);
    private ITextComponent customName;

    public BlueprintArchiveBlockEntity() {
        super(CyberwareBlockEntities.BLUEPRINT_ARCHIVE.get());
    }

    @Override
    public int getContainerSize() {
        return SIZE;
    }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    @Nonnull
    public ItemStack getItem(int index) {
        return items.get(index);
    }

    @Override
    @Nonnull
    public ItemStack removeItem(int index, int count) {
        ItemStack stack = net.minecraft.inventory.ItemStackHelper.removeItem(items, index, count);
        if (!stack.isEmpty()) setChanged();
        return stack;
    }

    @Override
    @Nonnull
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack stack = net.minecraft.inventory.ItemStackHelper.takeItem(items, index);
        if (!stack.isEmpty()) setChanged();
        return stack;
    }

    @Override
    public void setItem(int index, @Nonnull ItemStack stack) {
        if (CyberwareItems.BLUEPRINT.get().equals(stack.getItem())
                || Items.PAPER.equals(stack.getItem())) {
            items.set(index, stack);
            if (stack.getCount() > getMaxStackSize()) stack.setCount(getMaxStackSize());
            setChanged();
        }
    }

    @Override
    public void load(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
        super.load(state, nbt);
        ItemStackHelper.loadAllItems(nbt, items);
        if (nbt.contains("CustomName", 8)) {
            this.customName = ITextComponent.Serializer.fromJson(nbt.getString("CustomName"));
        }
    }

    @Override
    @Nonnull
    public CompoundNBT save(@Nonnull CompoundNBT nbt) {
        super.save(nbt);
        ItemStackHelper.saveAllItems(nbt, items);
        if (this.customName != null) {
            nbt.putString("CustomName", ITextComponent.Serializer.toJson(this.customName));
        }
        return nbt;
    }

    @Override
    public boolean stillValid(@Nonnull PlayerEntity player) {
        if (this.level == null) return false;
        return !(player.distanceToSqr(this.worldPosition.getX() + 0.5,
                this.worldPosition.getY() + 0.5,
                this.worldPosition.getZ() + 0.5) > 64.0);
    }

    @Override
    @Nonnull
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("container." + OverclockedOrgans.MOD_ID + ".blueprint_archive");
    }

    @Override
    @Nonnull
    public ITextComponent getDisplayName() {
        return customName != null ?
                customName : getDefaultName();
    }

    public void setCustomName(@Nonnull ITextComponent name) {
        this.customName = name;
    }

    @Override
    @Nonnull
    protected Container createMenu(int id, @Nonnull PlayerInventory playerInventory) {
        return new BlueprintArchiveContainer(id, playerInventory, this);
    }

    @Override
    public void clearContent() {
        Collections.fill(items, ItemStack.EMPTY);
        setChanged();
    }
}
