package flaxbeard.cyberware.api;

import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.api.item.ICyberware;
import flaxbeard.cyberware.api.item.IDeconstructable;
import flaxbeard.cyberware.api.item.IMenuItem;
import flaxbeard.cyberware.common.CyberwareConfig;
import flaxbeard.cyberware.common.item.BlueprintItem;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class CyberwareAPI {
    @CapabilityInject(ICyberwareUserData.class)
    public static final Capability<ICyberwareUserData> CYBERWARE_CAPABILITY = null;
    /**
     * Store any functional data of your Cyberware in NBT under this tag, which will be cleared when new items are added or removed
     * to ensure stacking and such works
     */
    public static final String DATA_TAG = "cyberwareFunctionData";

    /**
     * Maximum Tolerance, per-player
     */
    public static final Attribute TOLERANCE_ATTR = new RangedAttribute("attribute." + OverclockedOrgans.MOD_ID + ".tolerance", CyberwareConfig.ESSENCE.get(), 0.0F, Double.MAX_VALUE);

    private static final Map<Item, ICyberware> linkedWare = new HashMap<>();

    /**
     * Determines if the inputted item stack can be destroyed in the Engineering Table,
     * meaning it implements IDeconstructable.
     *
     * @param world The world for the Recipe Manager
     * @param stack	The ItemStack to test
     * @return		If the stack can be deconstructed.
     */
    public static boolean canDeconstruct(World world, ItemStack stack) {
        return (!stack.isEmpty()
                && stack.getItem() instanceof IDeconstructable
                && ((IDeconstructable) stack.getItem())
                    .canDestroy(world, stack));
    }

    /**
     * Sets the HUD color for the Hudjack, radial menu, and other AR HUD elements
     *
     * @param color	A float representation of the desired color
     */
    @OnlyIn(Dist.CLIENT)
    public static void setHUDColor(float[] color) {
        PlayerEntity player = Minecraft.getInstance().player;
        LazyOptional<ICyberwareUserData> cyberwareUserData = CyberwareAPI.getCyberwareData(player);
        if (!cyberwareUserData.isPresent()) return;
        cyberwareUserData.orElseThrow(AssertionError::new).setHudColor(color);
    }

    /**
     * Sets the HUD color for the Hudjack, radial menu, and other AR HUD elements
     *
     * @param hexVal A hexadecimal representation of the desired color
     */
    @OnlyIn(Dist.CLIENT)
    public static void setHUDColor(int hexVal) {
        PlayerEntity player = Minecraft.getInstance().player;
        LazyOptional<ICyberwareUserData> cyberwareUserData = CyberwareAPI.getCyberwareData(player);
        if (!cyberwareUserData.isPresent()) return;
        cyberwareUserData.orElseThrow(AssertionError::new).setHudColor(hexVal);
    }

    @OnlyIn(Dist.CLIENT)
    public static void setHUDColor(float r, float g, float b) {
        setHUDColor(new float[] { r, g, b });
    }

    @OnlyIn(Dist.CLIENT)
    public static int getHUDColorHex() {
        PlayerEntity player = Minecraft.getInstance().player;
        LazyOptional<ICyberwareUserData> cyberwareUserData = CyberwareAPI.getCyberwareData(player);
        if (!cyberwareUserData.isPresent()) return 0;
        return cyberwareUserData.orElseThrow(AssertionError::new).getHudColorHex();
    }

    @OnlyIn(Dist.CLIENT)
    public static float[] getHUDColor() {
        PlayerEntity player = Minecraft.getInstance().player;
        LazyOptional<ICyberwareUserData> cyberwareUserData = CyberwareAPI.getCyberwareData(player);
        if (!cyberwareUserData.isPresent()) return new float[] { 0F, 0F, 0F };
        return cyberwareUserData.orElseThrow(AssertionError::new).getHudColor();
    }

    /**
     * Clears all NBT data from Cyberware related to its function, things like power storage or oxygen storage
     * This ensures that removed Cyberware will stack. This should only be called on Cyberware that is being removed
     * from the body or otherwise reset - otherwise it may interrupt functionality.
     *
     * @param stack	The ItemStack to sanitize
     * @return		A sanitized version of the stack
     */
    public static ItemStack sanitize(@Nonnull ItemStack stack) {
        if (stack.isEmpty() || stack.getTag() == null) return stack;
        CompoundNBT tagCompound = stack.getTag();
        if (tagCompound.contains(DATA_TAG)) {
            tagCompound.remove(DATA_TAG);
        }
        if (tagCompound.isEmpty()) {
            stack.setTag(null);
        }
        return stack;
    }

    /**
     * Gets the NBT data for Cyberware related to its function. This data is removed when a piece of Cyberware
     * is removed, and is not counted when determining whether Cyberware stacks are the same for purposes of merging
     * and such. This function will create a data tag if one does not exist.
     *
     * @param stack	The ItemStack for which you want the data
     * @return		The data, in the form of an CompoundNBT
     */
    @Nonnull
    public static CompoundNBT getCyberwareNBT(@Nonnull ItemStack stack) {
        CompoundNBT tagCompound = stack.getTag();
        if (tagCompound == null) {
            tagCompound = new CompoundNBT();
            stack.setTag(tagCompound);
        }
        if (!tagCompound.contains(DATA_TAG)) {
            tagCompound.put(DATA_TAG, new CompoundNBT());
        }

        return tagCompound.getCompound(DATA_TAG);
    }

    public static boolean areCyberwareStacksEqual(@Nonnull ItemStack stack1, @Nonnull ItemStack stack2) {
        if (stack1.isEmpty() || stack2.isEmpty()) return false;

        ItemStack sanitized1 = sanitize(stack1.copy());
        ItemStack sanitized2 = sanitize(stack2.copy());
        return sanitized1.getItem() == sanitized2.getItem()
                && ItemStack.isSameIgnoreDurability(stack1, stack2);
    }

    /**
     * Determines if the inputted item stack is Cyberware. This means it's item either
     * implements ICyberware or is linked to one (in the case of vanilla items)
     *
     * @param stack	The ItemStack to test
     * @return		If the stack is valid Cyberware
     */
    public static boolean isCyberware(@Nullable ItemStack stack) {
        if (stack == null) return false;
        return !stack.isEmpty()
                && (stack.getItem() instanceof ICyberware
                || getLinkedWare(stack) != null);
    }

    /**
     * Returns an instance of ICyberware linked with an itemstack, usually
     * the item which extends ICyberware, though it may be a standalone
     * ICyberware-implementing object
     *
     * @param stack	The ItemStack, from which the linked ICyberware is found
     * @return		The linked instance of ICyberware
     */
    public static ICyberware getCyberware(@Nonnull ItemStack stack) {
        if (!stack.isEmpty()) {
            if (stack.getItem() instanceof ICyberware) return (ICyberware) stack.getItem();
            if (getLinkedWare(stack) != null) return getLinkedWare(stack);
        }
        throw new RuntimeException("Cannot call getCyberware on a non-cyberware item!");
    }

    @Nullable
    private static ICyberware getLinkedWare(ItemStack stack) {
        if (stack.isEmpty()) return null;
        return getWareFromKey(stack);
    }

    @Nullable
    private static ICyberware getWareFromKey(@Nonnull ItemStack stack) {
        if (stack.isEmpty()) return null;
        return linkedWare.get(stack.getItem());
    }

    /**
     * Returns a list of ItemStacks containing the components of a destructible
     * item.
     *
     * @param world The World for the recipe manager
     * @param stack	The ItemStack to test
     * @return		The components of the item
     */
    @Nonnull
    public static NonNullList<ItemStack> getComponents(World world, ItemStack stack) {
        if (!(stack.getItem() instanceof BlueprintItem)) return NonNullList.create();
        return ((IDeconstructable)((BlueprintItem) stack.getItem())
                .getResult(stack).getItem())
                .getComponents(world, ((BlueprintItem) stack.getItem()).getResult(stack));
    }

    /**
     * Links an Item to an instance of ICyberware. This option is generally worse than
     * implementing ICyberware in your Item, but if you don't have access to the Item it's the
     * best option.
     *
     * @param item	The Item to link
     * @param link	An instance of ICyberware to link it to
     */
    public static void linkCyberware(@Nonnull Item item, @Nonnull ICyberware link) {
        linkedWare.put(item, link);
    }

    @Nonnull
    @SuppressWarnings("DataFlowIssue")
    public static LazyOptional<ICyberwareUserData> getCyberwareData(@Nullable Entity targetEntity) {
        if (targetEntity == null) return LazyOptional.empty();
        return targetEntity.getCapability(CYBERWARE_CAPABILITY, null);
    }

    public static void useActiveItem(Entity entity, ItemStack stack) {
        ((IMenuItem) stack.getItem()).use(entity, stack);
    }
}
