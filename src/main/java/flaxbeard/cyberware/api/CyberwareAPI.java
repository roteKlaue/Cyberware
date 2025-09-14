package flaxbeard.cyberware.api;

import flaxbeard.cyberware.api.item.IDeconstructable;
import flaxbeard.cyberware.common.item.BlueprintItem;
import flaxbeard.cyberware.common.misc.recipe.DestructingRecipe;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class CyberwareAPI {
    public static final String DATA_TAG = "cyberwareFunctionData";

    public static boolean canDeconstruct(World world, ItemStack stack) {
        return (!stack.isEmpty()
                && stack.getItem() instanceof IDeconstructable
                && ((IDeconstructable) stack.getItem()).canDestroy(world, stack));
    }

    /**
     * Gets the NBT data for Cyberware related to its function. This data is removed when a piece of Cyberware
     * is removed, and is not counted when determining whether Cyberware stacks are the same for purposes of merging
     * and such. This function will create a data tag if one does not exist.
     *
     * @param stack	The ItemStack for which you want the data
     * @return		The data, in the form of an NBTTagCompound
     */
    @Nonnull
    public static CompoundNBT getCyberwareNBT(@Nonnull ItemStack stack)
    {
        CompoundNBT tagCompound = stack.getTag();
        if (tagCompound == null)
        {
            tagCompound = new CompoundNBT();
            stack.setTag(tagCompound);
        }
        if (!tagCompound.contains(DATA_TAG))
        {
            tagCompound.put(DATA_TAG, new CompoundNBT());
        }

        return tagCompound.getCompound(DATA_TAG);
    }

    @Nonnull
    public static NonNullList<ItemStack> getComponents(World world, ItemStack blueprintItem) {
        return world.getRecipeManager()
                .getAllRecipesFor(DestructingRecipe.Type.INSTANCE)
                .stream()
                .filter(r -> ItemStack.isSameIgnoreDurability(r.getInput(), ((BlueprintItem) blueprintItem.getItem())
                        .getResult(blueprintItem)))
                .findFirst()
                .map(DestructingRecipe::getOutputs)
                .orElse(NonNullList.create());
    }

    public static ICyberwareUserData getCapabilityOrNull(LivingEntity entityLiving) {
        return null; // TODO: implement
    }
}
