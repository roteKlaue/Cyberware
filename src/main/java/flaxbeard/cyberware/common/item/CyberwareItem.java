package flaxbeard.cyberware.common.item;

import flaxbeard.cyberware.api.item.ICyberware;
import flaxbeard.cyberware.api.item.IDeconstructable;
import lombok.Builder;
import lombok.Getter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.RegistryObject;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Builder
@Getter
public class CyberwareItem extends CyberwareBaseItem implements ICyberware, IDeconstructable {
    private final BodySlot slot;
    private final int essence;
    private final @Nonnull List<RegistryObject<Item>> incompatible;
    private final @Nullable List<RegistryObject<Item>> requirement;
    private final List<RegistryObject<CyberwareBaseItem>> components;
    private final Quality quality;

    public CyberwareItem(BodySlot slot, int essence,
                          List<RegistryObject<Item>> incompatible,
                          @Nullable List<RegistryObject<Item>> requirement,
                          List<RegistryObject<CyberwareBaseItem>> components,
                          Quality quality) {
        Objects.requireNonNull(components, "components must not be null");

        boolean invalidFound = components.stream()
                .anyMatch(c -> !CyberwareItems.COMPONENT.contains(c));
        if (invalidFound) {
            throw new IllegalArgumentException("CyberwareItem may only consist of registered components.");
        }

        this.components = new ArrayList<>(components);
        this.slot = slot;
        this.essence = essence;
        this.requirement = requirement;
        this.incompatible = incompatible;
        this.quality = quality;
    }

    @Override
    public boolean canDestroy(ItemStack stack) {
        return true;
    }

    @Override
    public NonNullList<ItemStack> getComponents(ItemStack stack) {
        NonNullList<ItemStack> result = NonNullList.create();
        for (RegistryObject<CyberwareBaseItem> regObj : components) {
            CyberwareBaseItem item = regObj.get();
            result.add(new ItemStack(item));
        }
        return result;
    }

    @Override
    public BodySlot getSlot(ItemStack stack) {
        return slot;
    }

    @Override
    public int installedStackSize(ItemStack stack) {
        return 0;
    }

    @Override
    public NonNullList<NonNullList<ItemStack>> required(ItemStack stack) {
        return NonNullList.create();
    }

    @Override
    public boolean isIncompatible(ItemStack stack, ItemStack comparison) {
        return false;
    }

    @Override
    public boolean isEssential(ItemStack stack) {
        return false;
    }

    @Override
    public List<String> getInfo(ItemStack stack) {
        return Collections.emptyList();
    }

    @Override
    public int getCapacity(ItemStack wareStack) {
        return 0;
    }

    @Override
    public Quality getQuality() {
        return quality;
    }

    @Override
    public void onAdded(LivingEntity entityLivingBase, ItemStack stack) {

    }

    @Override
    public void onRemoved(LivingEntity entityLivingBase, ItemStack stack) {

    }

    @Override
    public int getEssenceCost(ItemStack stack) {
        return 0;
    }
}
