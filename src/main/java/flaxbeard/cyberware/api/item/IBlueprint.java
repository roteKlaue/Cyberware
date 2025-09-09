package flaxbeard.cyberware.api.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public interface IBlueprint
{
    ItemStack getResult(ItemStack stack);
    default ItemStack getIconForDisplay(ItemStack stack)
    {
        return ItemStack.EMPTY;
    }
    default NonNullList<ItemStack> getRequirementsForDisplay(ItemStack stack)
    {
        return NonNullList.create();
    }
}
