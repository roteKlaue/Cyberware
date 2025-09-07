package flaxbeard.cyberware.api.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.Objects;

public interface IDeconstructable {
    boolean canDestroy(ItemStack stack);
    NonNullList<ItemStack> getComponents(ItemStack stack);
    default IDeconstructable getManufactured() { return this; }
}

