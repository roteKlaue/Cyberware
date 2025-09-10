package flaxbeard.cyberware.api.item;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public interface IBlueprint
{
    ItemStack getResult(ItemStack stack);
    default ItemStack getIconForDisplay(ItemStack stack)
    {
        return ItemStack.EMPTY;
    }
    NonNullList<ItemStack> getRequirementsForDisplay(World world, ItemStack stack);
}
