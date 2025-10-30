package flaxbeard.cyberware.common.item;

import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.api.item.ICyberware;
import flaxbeard.cyberware.api.item.IDeconstructable;
import lombok.Getter;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

@Getter
public class CyberwareItem extends CyberwareBaseItem implements ICyberware, IDeconstructable {
    private final BodySlot slot;
    private final int essence;
    private final @Nonnull List<RegistryObject<Item>> incompatible;
    private final @Nullable List<RegistryObject<Item>> requirement;
    private final boolean isManufactured;
    private final @Nullable RegistryObject<CyberwareItem> manufactured;

    public CyberwareItem(@Nonnull BodySlot slot, int essence,
                         @Nonnull List<RegistryObject<Item>> incompatible,
                         @Nullable List<RegistryObject<Item>> requirement) {

        this(slot, essence, incompatible, requirement, null);
    }

    public CyberwareItem(@Nonnull BodySlot slot, int essence,
                         @Nonnull List<RegistryObject<Item>> incompatible,
                         @Nullable List<RegistryObject<Item>> requirement,
                         @Nullable RegistryObject<CyberwareItem> manufactured) {
        this.slot = slot;
        this.essence = essence;
        this.requirement = requirement == null ? new ArrayList<>() : requirement;
        this.incompatible = incompatible;
        this.manufactured = manufactured;
        this.isManufactured = manufactured != null;
    }

    public CyberwareItem(@Nonnull CyberwareItem cyberwareItem) {
        this.slot = cyberwareItem.slot;
        this.essence = cyberwareItem.essence;
        this.requirement = new ArrayList<>(cyberwareItem.requirement == null?
                new ArrayList<>() : cyberwareItem.requirement);
        this.incompatible = new ArrayList<>(cyberwareItem.incompatible);
        this.manufactured = cyberwareItem.manufactured;
        this.isManufactured = manufactured != null;
    }

    @Override
    public BodySlot getSlot() {
        return slot;
    }

    @Override
    public int installedStackSize(ItemStack stack) {
        return 1;
    }

    @Override
    public NonNullList<NonNullList<ItemStack>> required() {
        return NonNullList.create();
    }

    @Override
    public boolean isIncompatible(ItemStack other) {
        return other.getItem().getClass().isInstance(this);
    }

    @Override
    public boolean isEssential() {
        return false;
    }

    @Override
    public int getCapacity(ItemStack wareStack) {
        return 0;
    }

    @Override
    public IDeconstructable getManufactured() {
        return isManufactured ? this
                : Objects.requireNonNull(this.manufactured).get();
    }

    @Override
    public void onAdded(LivingEntity livingEntity, ItemStack stack) {

    }

    @Override
    public void onRemoved(LivingEntity livingEntity, ItemStack stack) {

    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable World worldIn,
                                @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
        if (!Screen.hasShiftDown()) {
            tooltip.add(new TranslationTextComponent("tooltip.overclockedorgans.shift_prompt")
                    .withStyle(TextFormatting.GRAY));
            return;
        }
        tooltip.addAll(getDescription(stack));
    }

    public List<ITextComponent> getStackDesc() {
        List<ITextComponent> toReturn = new ArrayList<>();

        String key = "tooltip." + OverclockedOrgans.MOD_ID + "." + Objects.requireNonNull(this.getRegistryName()).getPath();
        ITextComponent localized = new TranslationTextComponent(key);

        for (String line : localized.getString().split("\\\\n")) {
            if (!line.isEmpty()) {
                toReturn.add(new TranslationTextComponent(line));
            }
        }

        return toReturn;
    }

    public List<ITextComponent> getDescription(ItemStack stack) {
        List<ITextComponent> toReturn = getStackDesc();

        // --- Max Install ---
        if (installedStackSize(stack) > 1) {
            toReturn.add(new TranslationTextComponent("tooltip." + OverclockedOrgans.MOD_ID + ".max_install",
                    installedStackSize(stack))
                    .withStyle(TextFormatting.BLUE));
        }

        // --- Power Consumption ---
        boolean hasPowerConsumption = false;
        StringBuilder toAddPowerConsumption = new StringBuilder();
        for (int i = 0; i < installedStackSize(stack); i++) {
            ItemStack temp = stack.copy();
            temp.setCount(i + 1);
            int cost = this.getPowerConsumption();
            if (cost > 0) hasPowerConsumption = true;

            if (i != 0) {
                toAddPowerConsumption.append(new TranslationTextComponent("tooltip." + OverclockedOrgans.MOD_ID + ".joiner").getString());
            }
            toAddPowerConsumption.append(" ").append(cost);
        }

        if (hasPowerConsumption) {
            String key = hasCustomPowerMessage()
                    ? "tooltip." + OverclockedOrgans.MOD_ID + "." + Objects.requireNonNull(this.getRegistryName()).getPath() + ".power_consumption"
                    : "tooltip." + OverclockedOrgans.MOD_ID + ".power_consumption";

            toReturn.add(new TranslationTextComponent(key, toAddPowerConsumption.toString())
                    .withStyle(TextFormatting.GREEN));
        }

        // --- Power Production ---
        boolean hasPowerProduction = false;
        StringBuilder toAddPowerProduction = new StringBuilder();
        for (int i = 0; i < installedStackSize(stack); i++) {
            ItemStack temp = stack.copy();
            temp.setCount(i + 1);
            int cost = this.getPowerProduction();
            if (cost > 0) hasPowerProduction = true;

            if (i != 0) {
                toAddPowerProduction.append(new TranslationTextComponent("tooltip." + OverclockedOrgans.MOD_ID + ".joiner").getString());
            }
            toAddPowerProduction.append(" ").append(cost);
        }

        if (hasPowerProduction) {
            String toTranslate = hasCustomPowerMessage()
                    ? "tooltip." + OverclockedOrgans.MOD_ID + "." + Objects.requireNonNull(this.getRegistryName()).getPath() + ".power_production"
                    : "tooltip." + OverclockedOrgans.MOD_ID + ".power_production";

            toReturn.add(new TranslationTextComponent(toTranslate, toAddPowerProduction.toString())
                    .withStyle(TextFormatting.GREEN));
        }

        // --- Capacity ---
        if (getCapacity(stack) > 0) {
            String toTranslate = hasCustomCapacityMessage()
                    ? "tooltip." + OverclockedOrgans.MOD_ID + "." + Objects.requireNonNull(this.getRegistryName()).getPath() + ".capacity"
                    : "tooltip." + OverclockedOrgans.MOD_ID + ".capacity";

            toReturn.add(new TranslationTextComponent(toTranslate, getCapacity(stack))
                    .withStyle(TextFormatting.GREEN));
        }

        // --- Essence ---
        boolean hasEssenceCost = false;
        boolean essenceCostNegative = true;
        StringBuilder toAddEssence = new StringBuilder();

        for (int i = 0; i < installedStackSize(stack); i++) {
            ItemStack temp = stack.copy();
            temp.setCount(i + 1);
            int cost = this.getEssenceCost(temp);

            if (cost != 0) hasEssenceCost = true;
            if (cost < 0) essenceCostNegative = false;

            if (i != 0) {
                toAddEssence.append(new TranslationTextComponent("tooltip." + OverclockedOrgans.MOD_ID + ".joiner").getString());
            }
            toAddEssence.append(" ").append(Math.abs(cost));
        }

        if (hasEssenceCost) {
            String key = essenceCostNegative
                    ? "tooltip." + OverclockedOrgans.MOD_ID + ".essence"
                    : "tooltip." + OverclockedOrgans.MOD_ID + ".essence_add";

            toReturn.add(new TranslationTextComponent(key, toAddEssence.toString())
                    .withStyle(TextFormatting.DARK_PURPLE));
        }

        return toReturn;
    }

    public int getPowerConsumption() {
        return 0;
    }

    public int getPowerProduction() {
        return 0;
    }

    public boolean hasCustomPowerMessage() {
        return false;
    }

    public boolean hasCustomCapacityMessage() {
        return false;
    }

    @Override
    public int getEssenceCost(ItemStack stack) {
        return 0;
    }

    public static CyberwareItemBuilder builder()  {
        return CyberwareItemBuilder.create();
    }

    public static class CyberwareItemBuilder {
        private BodySlot slot;
        private int essence;
        private Set<RegistryObject<Item>> incompatible = new HashSet<>();
        private List<RegistryObject<Item>> requirement = null;
        private RegistryObject<CyberwareItem> manufactured = null;

        private CyberwareItemBuilder() {}

        public static CyberwareItemBuilder create() {
            return new CyberwareItemBuilder();
        }

        public CyberwareItemBuilder slot(BodySlot slot) {
            if (slot == null) return this;
            this.slot = slot;
            return this;
        }

        public CyberwareItemBuilder essence(int essence) {
            if (essence < 0) return this;
            this.essence = essence;
            return this;
        }

        public CyberwareItemBuilder incompatible(List<RegistryObject<Item>> incompatible) {
            if (incompatible == null || incompatible.isEmpty()) return this;
            this.incompatible.addAll(incompatible);
            return this;
        }

        public CyberwareItemBuilder addIncompatible(RegistryObject<Item> item) {
            if (item == null) return this;
            this.incompatible.add(item);
            return this;
        }

        public CyberwareItemBuilder requirement(List<RegistryObject<Item>> requirement) {
            if (requirement == null || requirement.isEmpty()) return this;
            this.requirement = requirement;
            return this;
        }

        public CyberwareItemBuilder addRequirement(RegistryObject<Item> item) {
            if (item == null) return this;
            if (this.requirement == null) this.requirement = new ArrayList<>();
            this.requirement.add(item);
            return this;
        }

        public CyberwareItemBuilder manufactured(RegistryObject<CyberwareItem> manufactured) {
            if (manufactured == null) return this;
            this.manufactured = manufactured;
            return this;
        }

        public CyberwareItemBuilder manufactured(CyberwareItem item) {
            this.manufactured = RegistryObject.of(item.getRegistryName(), ForgeRegistries.ITEMS);
            return this;
        }

        public CyberwareItem build() {
            if (slot == null) throw new IllegalStateException("CyberwareItem requires a BodySlot.");
            if (incompatible == null) throw new IllegalStateException("CyberwareItem requires a non-null incompatible list.");
            return new CyberwareItem(slot, essence, new ArrayList<>(incompatible), requirement, manufactured);
        }
    }
}
