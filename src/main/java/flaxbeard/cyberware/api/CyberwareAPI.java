package flaxbeard.cyberware.api;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;

public class CyberwareAPI {
    public static final String DATA_TAG = "cyberwareFunctionData";

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
}
