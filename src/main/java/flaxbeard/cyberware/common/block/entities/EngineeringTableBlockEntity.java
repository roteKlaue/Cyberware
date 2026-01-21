package flaxbeard.cyberware.common.block.entities;

import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.item.IDeconstructable;
import flaxbeard.cyberware.client.gui.EngineeringTableContainer;
import flaxbeard.cyberware.common.CyberwareConfig;
import flaxbeard.cyberware.common.item.BlueprintItem;
import flaxbeard.cyberware.common.item.CyberwareItems;
import flaxbeard.cyberware.common.misc.recipe.EngineeringRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class EngineeringTableBlockEntity extends NameContainerProvider<EngineeringTableBlockEntity> implements ITickableTileEntity {
    private static final int SLOT_COUNT = 10;
    public final EngineeringTableItemStackHandler slots = new EngineeringTableItemStackHandler(this);
    private final LazyOptional<IItemHandler> topHandler = LazyOptional.of(() -> new RangedWrapper(slots, 0, 1));
    private final LazyOptional<IItemHandler> sideHandler = LazyOptional.of(() -> new RangedWrapper(slots, 1, 2));
    private final LazyOptional<IItemHandler> bottomHandler = LazyOptional.of(() -> new RangedWrapper(slots, 2, 8));

    public HashMap<String, BlockPos> lastPlayerArchive = new HashMap<>();
    public float clickedTime;

    public EngineeringTableBlockEntity() {
        super(CyberwareBlockEntities.ENGINEERING_TABLE.get(), "engineering_table",
                EngineeringTableContainer::new, EngineeringTableBlockEntity.class);
    }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side == Direction.UP) {
                return topHandler.cast();
            } else if (side == Direction.DOWN) {
                return bottomHandler.cast();
            } else if (side != null) {
                return sideHandler.cast();
            } else {
                return LazyOptional.of(() -> slots).cast();
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void load(@Nonnull BlockState state, @Nonnull CompoundNBT tag) {
        super.load(state, tag);

        if (tag.contains("inv")) {
            slots.deserializeNBT(tag.getCompound("inv"));
        }

        if (tag.contains("clickedTime")) {
            clickedTime = tag.getFloat("clickedTime");
        }
    }

    @Override
    @Nonnull
    public CompoundNBT save(@Nonnull CompoundNBT tag) {
        super.save(tag);
        tag.put("inv", slots.serializeNBT());
        tag.putFloat("clickedTime", clickedTime);
        return tag;
    }

    @Override
    @Nonnull
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = super.getUpdateTag();
        tag.put("inv", slots.serializeNBT());
        tag.putFloat("clickedTime", clickedTime);
        return tag;
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT tag = new CompoundNBT();
        tag.put("inv", slots.serializeNBT());
        tag.putFloat("clickedTime", clickedTime);
        return new SUpdateTileEntityPacket(this.worldPosition, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT tag = pkt.getTag();

        if (tag.contains("clickedTime")) {
            try {
                this.clickedTime = tag.getFloat("clickedTime");
                OverclockedOrgans.LOGGER.info("[CLIENT] synced clickedTime={} for pos={}", this.clickedTime, this.worldPosition);
            } catch (Exception e) {
                OverclockedOrgans.LOGGER.warn("[CLIENT] failed to read clickedTime from packet", e);
            }
        }

        if (tag.contains("inv")) {
            try {
                this.slots.deserializeNBT(tag.getCompound("inv"));
                OverclockedOrgans.LOGGER.info("[CLIENT] synced inventory for pos={}", this.worldPosition);
            } catch (Exception e) {
                OverclockedOrgans.LOGGER.warn("[CLIENT] failed to read inventory from packet", e);
            }
        }
    }

    @Override
    public void tick() {
        if (level == null || level.isClientSide) return;

        boolean powered = level.hasNeighborSignal(worldPosition)
                || level.hasNeighborSignal(worldPosition.below());
        if (!powered) return;
        long last = (long) clickedTime;
        long now = level.getGameTime();
        if (now - last >= 25) {
            destruct();
        }
    }

    public boolean stillValid(PlayerEntity player) {
        return (this.level != null) &&
                !(player.distanceToSqr(worldPosition.getX() + 0.5,
                worldPosition.getY() + 0.5,
                worldPosition.getZ() + 0.5) > 64.0);
    }

    public void destruct() {
        ItemStack stack = slots.getStackInSlot(0);
        if (stack.isEmpty() || this.level == null) return;

        Item item = stack.getItem();
        if (!(item instanceof IDeconstructable)) return;

        IDeconstructable deconstruct = (IDeconstructable) item;
        NonNullList<ItemStack> components = deconstruct.getComponents(level, stack);

        if (components == null || components.isEmpty()) return;

        List<ItemStack> drops = new ArrayList<>();
        for (ItemStack component : components) {
            if (!component.isEmpty()) {
                for (int i = 0; i < component.getCount(); i++) {
                    ItemStack copy = component.copy();
                    copy.setCount(1);
                    drops.add(copy);
                }
            }
        }

        int numToRemove = 1;

        switch (level.getDifficulty()) {
            case HARD:
            case NORMAL: numToRemove = 2; break;
        }

        if (slots.getStackInSlot(0).isDamageableItem()) {
            float percent = (stack.getDamageValue() * 1F / stack.getMaxDamage());
            int addl = (int) (drops.size() * percent);
            addl = Math.max(0, addl - 1);
            numToRemove += addl;
        }

        numToRemove = Math.min(numToRemove, Math.max(0, drops.size() - 1));

        Random rand = this.level.getRandom();
        for (int i = 0; i < numToRemove && !drops.isEmpty(); i++) {
            drops.remove(rand.nextInt(drops.size()));
        }

        ItemStackHandler checkHandler = new ItemStackHandler(6);
        for (int i = 0; i < 6; i++) {
            checkHandler.setStackInSlot(i, slots.getStackInSlot(i + 2).copy());
        }

        boolean canInsert = true;
        for (ItemStack drop : drops) {
            ItemStack left = checkHandler.insertItem(0, drop.copy(), false);
            for (int i = 1; i < 6 && !left.isEmpty(); i++) {
                left = checkHandler.insertItem(i, left, false);
            }
            if (!left.isEmpty()) {
                canInsert = false;
                break;
            }
        }

        if (!canInsert) return;

        shrinkStack(slots, 0);
        clickedTime = this.level.getGameTime();

        for (ItemStack drop : drops) {
            ItemStack left = drop.copy();
            for (int i = 2; i < 8 && !left.isEmpty(); i++) {
                left = slots.insertItem(i, left, false);
            }
        }

        makeBlueprint(item);
        setChanged();

        if (!this.level.isClientSide) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 2);
        }
        spawnItemBreakParticles(this.level, item, this.worldPosition.getX() + 0.5, this.worldPosition.getY(), this.worldPosition.getZ() + 0.5, 10);
        playBreakingSound();
    }

    private static void shrinkStack(ItemStackHandler inventory, int slot) {
        ItemStack current = inventory.getStackInSlot(slot);
        current.shrink(1);
        if (current.getCount() <= 0) {
            current = ItemStack.EMPTY;
        }
        inventory.setStackInSlot(slot, current);
    }

    private void makeBlueprint(Item item) {
        if (this.level == null
                || this.level.isClientSide
                || !slots.getStackInSlot(8).isEmpty()
                || slots.getStackInSlot(1).isEmpty()) return;

        float result = this.level.random.nextFloat();
        if (result < (CyberwareConfig.ENGINEERING_CHANCE.get() / 100F)) {
            ItemStack stackBlueprint = BlueprintItem.makeBlueprint(this.level, new ItemStack(item));
            slots.setStackInSlot(8, stackBlueprint);
            shrinkStack(slots, 1);
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 2);
        }
    }

    private static void spawnItemBreakParticles(World world, Item prototype, double x, double y, double z, int count) {
        ItemStack particleStack = new ItemStack(prototype);
        ItemParticleData data = new ItemParticleData(ParticleTypes.ITEM, particleStack);

        if (world instanceof ServerWorld) {
            ServerWorld sw = (ServerWorld) world;
            sw.sendParticles(data, x, y, z, 10, 0.25D, 0.25D, 0.25D, 0.06D);
        } else {
            for (int i = 0; i < count; i++) {
                double vx = (world.getRandom().nextDouble() - 0.5) * 0.2;
                double vy = world.getRandom().nextDouble() * 0.2;
                double vz = (world.getRandom().nextDouble() - 0.5) * 0.2;
                world.addParticle(data, x, y, z, vx, vy, vz);
            }
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        topHandler.invalidate();
        sideHandler.invalidate();
        bottomHandler.invalidate();
    }

    public IInventory getInventory() {
        IInventory inv = new Inventory(7);
        for (int i = 0; i < 6; i++) {
            inv.setItem(i, slots.getStackInSlot(i + 2).copy());
        }
        inv.setItem(6, slots.getStackInSlot(8).copy());
        return inv;
    }

    @Nonnull
    public ItemStack getCraftingResult() {
        if (level == null) return ItemStack.EMPTY;

        IInventory inv = getInventory();
        return level.getRecipeManager()
                .getRecipeFor(EngineeringRecipe.Type.INSTANCE, inv, level)
                .map(r -> r.assemble(inv))
                .orElse(ItemStack.EMPTY);
    }

    public void playBreakingSound() {
        if (this.level == null || this.level.isClientSide) return;

        ServerWorld sw = (ServerWorld) this.level;

        double x = this.worldPosition.getX() + 0.5;
        double y = this.worldPosition.getY() + 1.0;
        double z = this.worldPosition.getZ() + 0.5;

        sw.playSound(null, x, y, z, SoundEvents.PISTON_EXTEND, SoundCategory.BLOCKS, 1.0F, 1.0F);
        sw.playSound(null, x, y, z, SoundEvents.ITEM_BREAK, SoundCategory.BLOCKS, 1.0F, .5F);
    }

    public void extractCrafts(int count) {
        if (this.level == null || this.level.isClientSide) return;

        for (int c = 0; c < count; c++) {
            IInventory inv = getInventory();

            this.level.getRecipeManager()
                    .getRecipeFor(EngineeringRecipe.Type.INSTANCE, inv, this.level)
                    .ifPresent(recipe -> {
                        for (int i = 0; i < 6; i++) {
                            ItemStack current = slots.getStackInSlot(i + 2);
                            if (!current.isEmpty()) {
                                current.shrink(1);
                                if (current.getCount() <= 0) current = ItemStack.EMPTY;
                                slots.setStackInSlot(i + 2, current);
                            }
                        }

                        NonNullList<ItemStack> remaining = recipe.getRemainingItems(inv);
                        for (int i = 0; i < remaining.size() && i < 6; i++) {
                            slots.setStackInSlot(i + 2, remaining.get(i));
                        }
                    });
        }

        slots.setStackInSlot(9, getCraftingResult());

        setChanged();
        if (level != null) level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
    }

    public void refreshCraftingResult() {
        slots.refreshCraftingResult(4);
    }

    public static class EngineeringTableItemStackHandler extends ItemStackHandler {
        private final EngineeringTableBlockEntity entity;

        public EngineeringTableItemStackHandler(EngineeringTableBlockEntity engineeringTableBlockEntity) {
            super(SLOT_COUNT);
            this.entity = engineeringTableBlockEntity;
        }

        public boolean isItemValidForSlot(int slot, ItemStack stack) {
            validateSlotIndex(slot);

            switch (slot) {
                case 0:
                    return CyberwareAPI.canDeconstruct(entity.level, stack);
                case 1:
                    return stack.getItem().equals(Items.PAPER);
                case 9:
                    return false; // slot for crafting output
                case 8:
                    return stack.getItem().equals(CyberwareItems.BLUEPRINT.get());
                default:
                    return true;
            }
        }

        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            refreshCraftingResult(slot);
        }

        public void refreshCraftingResult(int slot) {
            entity.setChanged();

            if (slot >= 2 && slot <= 8) {
                ItemStack result = entity.getCraftingResult();
                setStackInSlot(9, result);
            }
        }

        @Override
        @Nonnull
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (slot == 9) return stack;
            return super.insertItem(slot, stack, simulate);
        }
    }
}
