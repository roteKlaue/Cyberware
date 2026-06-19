package flaxbeard.cyberware.common.item;

import flaxbeard.cyberware.api.item.ICyberware;
import flaxbeard.cyberware.api.item.IDeconstructable;
import flaxbeard.cyberware.common.misc.NNLUtil;
import lombok.Getter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.RegistryObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CyberwareItem extends Item implements ICyberware, IDeconstructable {
    private final BodySlot slot;
    private final int essence;
    private final int capacity;
    private final int powerConsumption;
    private final int powerProduction;
    private final int maxInstallations;
    private final boolean essential;
    private final boolean isSalvaged;

    private final NonNullList<NonNullList<ItemStack>> requirements;
    private final List<RegistryObject<? extends Item>> incompatible;
    private final RegistryObject<? extends IDeconstructable> manufactured;

    public CyberwareItem(Properties properties, CyberwareProperties cyberwareProperties) {
        super(properties);

        this.slot = cyberwareProperties.slot;
        this.essence = cyberwareProperties.essence;
        this.capacity = cyberwareProperties.capacity;
        this.powerConsumption = cyberwareProperties.powerConsumption;
        this.powerProduction = cyberwareProperties.powerProduction;
        this.maxInstallations = cyberwareProperties.maxInstallations;
        this.essential = cyberwareProperties.essential;
        this.requirements = cyberwareProperties.requirements;
        this.incompatible = cyberwareProperties.incompatible;
        this.isSalvaged = cyberwareProperties.isSalvaged;
        this.manufactured = cyberwareProperties.manufactured;
    }

    public CyberwareItem(CyberwareProperties cyberwareProperties) {
        this(new Item.Properties().tab(cyberwareProperties.isSalvaged
                ? CreativeModeTabs.SALVAGED_GROUP
                : CreativeModeTabs.MANUFACTURED_GROUP),
                cyberwareProperties);
    }

    @Override
    public BodySlot getSlot() {
        return slot;
    }

    @Override
    public int installedStackSize(ItemStack stack) {
        return maxInstallations;
    }

    @Override
    public NonNullList<NonNullList<ItemStack>> required() {
        return requirements;
    }

    @Override
    public boolean isIncompatible(ItemStack other) {
        if (!(other.getItem() instanceof CyberwareItem)) {
            return false;
        }

        CyberwareItem otherCyberware = (CyberwareItem) other.getItem();
        if (otherCyberware.getClass() == getClass()
                && otherCyberware.isSalvaged != isSalvaged) {
            return true;
        }

        return incompatible.stream()
                .map(RegistryObject::get)
                .anyMatch(item -> item == other.getItem());
    }

    @Override
    public IDeconstructable getManufactured() {
        return isSalvaged ? manufactured.get() : this;
    }

    @Override
    public boolean isEssential() {
        return essential;
    }

    @Override
    public int getCapacity(ItemStack wareStack) {
        return capacity;
    }

    @Override
    public int getEssenceCost(ItemStack stack) {
        if (!isSalvaged) return essence;
        return essence + (int) Math.ceil(essence / 2F);
    }

    public int getPowerConsumption(ItemStack stack) {
        return powerConsumption;
    }

    public int getPowerProduction(ItemStack stack) {
        return powerProduction;
    }

    @Override
    public void onAdded(LivingEntity livingEntity, ItemStack stack) {
        // override in subclasses
    }

    @Override
    public void onRemoved(LivingEntity livingEntity, ItemStack stack) {
        // override in subclasses
    }

    public static class CyberwareProperties {
        private BodySlot slot = BodySlot.SKIN;
        private int essence = 0;
        private int capacity = 0;
        private int powerConsumption = 0;
        private int powerProduction = 0;
        private int maxInstallations = 1;
        private boolean essential = false;
        @Getter
        private boolean isSalvaged = false;

        private final NonNullList<NonNullList<ItemStack>> requirements = NonNullList.create();
        private final List<RegistryObject<? extends Item>> incompatible = new ArrayList<>();
        private RegistryObject<? extends IDeconstructable> manufactured = null;

        public CyberwareProperties slot(BodySlot slot) {
            this.slot = slot;
            return this;
        }

        public CyberwareProperties essence(int essence) {
            this.essence = essence;
            return this;
        }

        public CyberwareProperties capacity(int capacity) {
            this.capacity = capacity;
            return this;
        }

        public CyberwareProperties powerConsumption(int powerConsumption) {
            this.powerConsumption = powerConsumption;
            return this;
        }

        public CyberwareProperties powerProduction(int powerProduction) {
            this.powerProduction = powerProduction;
            return this;
        }

        public CyberwareProperties maxInstallations(int maxInstallations) {
            this.maxInstallations = Math.max(1, maxInstallations);
            return this;
        }

        public CyberwareProperties essential() {
            this.essential = true;
            return this;
        }

        public CyberwareProperties manufactured(RegistryObject<? extends IDeconstructable> manufactured) {
            this.manufactured = manufactured;
            return this;
        }

        public CyberwareProperties salvaged() {
            this.isSalvaged = true;
            return this;
        }

        public CyberwareProperties requires(ItemStack... stacks) {
            NonNullList<ItemStack> group = NNLUtil.fromArray(stacks);
            requirements.add(group);
            return this;
        }

        @SafeVarargs
        public final CyberwareProperties incompatibleWith(RegistryObject<? extends Item>... items) {
            Collections.addAll(incompatible, items);
            return this;
        }

        public CyberwareProperties copy() {
            CyberwareProperties copy = new CyberwareProperties();

            copy.slot = this.slot;
            copy.essence = this.essence;
            copy.capacity = this.capacity;
            copy.powerConsumption = this.powerConsumption;
            copy.powerProduction = this.powerProduction;
            copy.maxInstallations = this.maxInstallations;
            copy.essential = this.essential;
            copy.isSalvaged = this.isSalvaged;
            copy.manufactured = this.manufactured;

            for (NonNullList<ItemStack> group : this.requirements) {
                NonNullList<ItemStack> copiedGroup = NonNullList.create();

                for (ItemStack stack : group) {
                    copiedGroup.add(stack.copy());
                }

                copy.requirements.add(copiedGroup);
            }

            copy.incompatible.addAll(this.incompatible);

            return copy;
        }
    }
}
