package flaxbeard.cyberware.api.item;

import flaxbeard.cyberware.common.misc.recipe.DestructingRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public interface IDeconstructable {
    default boolean canDestroy(@Nonnull World world, @Nonnull ItemStack stack) {
        if (stack.isEmpty() || stack.getItem().getRegistryName() == null) return false;
        return world.getRecipeManager()
                .getAllRecipesFor(DestructingRecipe.Type.INSTANCE)
                .stream().anyMatch(r -> ItemStack.isSameIgnoreDurability(r.getInput(), stack));
    }

    default NonNullList<ItemStack> getComponents(@Nonnull World world, @Nonnull ItemStack stack) {
        return world.getRecipeManager()
                .getAllRecipesFor(DestructingRecipe.Type.INSTANCE)
                .stream()
                .filter(r -> ItemStack.isSameIgnoreDurability(r.getInput(), stack))
                .findFirst()
                .map(DestructingRecipe::getOutputs)
                .orElse(NonNullList.create());
    }

    default IDeconstructable getManufactured() { return this; }
}
