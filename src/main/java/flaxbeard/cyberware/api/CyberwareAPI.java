package flaxbeard.cyberware.api;

import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.api.item.ICyberware;
import flaxbeard.cyberware.api.item.IDeconstructable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;

public class CyberwareAPI {
    public static final String DATA_TAG = "cyberwareFunctionData";

    public static final ICyberware.Quality QUALITY_SCAVENGED =
            new ICyberware.Quality("quality." + OverclockedOrgans.MOD_ID + ".scavenged", "cyberware.quality.scavenged.name_modifier", "scavenged");

    public static final ICyberware.Quality QUALITY_MANUFACTURED =
            new ICyberware.Quality("quality." + OverclockedOrgans.MOD_ID + ".manufactured");

    private static final String NBT_QUALITY = "Quality";

    public static void setQuality(ItemStack stack, ICyberware.Quality quality) {
        stack.getOrCreateTag().putString(NBT_QUALITY, quality.getUnlocalizedName());
    }

    public static ICyberware.Quality getQuality(ItemStack stack) {
        if (stack.hasTag()) {
            assert stack.getTag() != null;
            if (stack.getTag().contains(NBT_QUALITY)) {
                String id = stack.getTag().getString(NBT_QUALITY);
                if ("scavenged".equals(id)) return QUALITY_SCAVENGED;
                if ("manufactured".equals(id)) return QUALITY_MANUFACTURED;
            }
        }
        return QUALITY_MANUFACTURED; // default
    }

    public static boolean canDeconstruct(ItemStack stack) {
        return (!stack.isEmpty()
                && stack.getItem() instanceof IDeconstructable
                && ((IDeconstructable) stack.getItem()).canDestroy(stack));
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
    public static NonNullList<ItemStack> getComponents(ItemStack blueprintItem) {
        return NonNullList.create();
    }
}
