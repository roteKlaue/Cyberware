package flaxbeard.cyberware.api.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface ICyberware
{
    BodySlot getSlot(ItemStack stack);
    int installedStackSize(ItemStack stack);
    NonNullList<NonNullList<ItemStack>> required(ItemStack stack);
    boolean isIncompatible(ItemStack stack, ItemStack comparison);
    boolean isEssential(ItemStack stack);
    List<String> getInfo(ItemStack stack);
    int getCapacity(ItemStack wareStack);


    /**
     * Returns a Quality object representing the quality of this stack - all
     * changes that this Quality has to function must be handled internally,
     * this is just for the tooltip and external factors. See CyberwareAPI for
     * the base Qualities.
     *
     * @param stack	The ItemStack to check
     * @return		An instance of Quality
     */
    Quality getQuality(ItemStack stack);

    ItemStack setQuality(ItemStack stack, Quality quality);
    boolean canHoldQuality(ItemStack stack, Quality quality);

    class Quality
    {
        private static Map<String, Quality> mapping = new HashMap<>();
        public static List<Quality> qualities = new ArrayList<>();
        private String unlocalizedName;
        private String nameModifier;
        private String spriteSuffix;

        public Quality(String unlocalizedName)
        {
            this(unlocalizedName, null, null);
        }

        public Quality(String unlocalizedName, String nameModifier, String spriteSuffix)
        {
            this.unlocalizedName = unlocalizedName;
            this.nameModifier = nameModifier;
            this.spriteSuffix = spriteSuffix;
            mapping.put(unlocalizedName, this);
            qualities.add(this);
        }

        public String getUnlocalizedName()
        {
            return unlocalizedName;
        }

        public static Quality getQualityFromString(String name)
        {
            if (mapping.containsKey(name))
            {
                return mapping.get(name);
            }
            return null;
        }

        public String getNameModifier()
        {
            return nameModifier;
        }

        public String getSpriteSuffix()
        {
            return spriteSuffix;
        }
    }

    enum BodySlot
    {
        EYES(12, "eyes"),
        CRANIUM(11, "cranium"),
        HEART(14, "heart"),
        LUNGS(15, "lungs"),
        LOWER_ORGANS(17, "lower_organs"),
        SKIN(18, "skin"),
        MUSCLE(19, "muscle"),
        BONE(20, "bone"),
        ARM(21, "arm", true, true),
        HAND(22, "hand", true, false),
        LEG(23, "leg", true, true),
        FOOT(24, "foot", true, false);

        private final int slotNumber;
        private final String name;
        private final boolean sidedSlot;
        private final boolean hasEssential;

        BodySlot(int slot, String name, boolean sidedSlot, boolean hasEssential)
        {
            this.slotNumber = slot;
            this.name = name;
            this.sidedSlot = sidedSlot;
            this.hasEssential = hasEssential;
        }

        BodySlot(int slot, String name)
        {
            this(slot, name, false, true);
        }

        public int getSlotNumber()
        {
            return slotNumber;
        }

        public static BodySlot getSlotByPage(int page)
        {
            for (BodySlot slot : values())
            {
                if (slot.getSlotNumber() == page)
                {
                    return slot;
                }
            }
            return null;
        }

        public String getName()
        {
            return name;
        }

        public boolean isSided()
        {
            return sidedSlot;
        }

        public boolean hasEssential()
        {
            return hasEssential;
        }
    }

    void onAdded(LivingEntity entityLivingBase, ItemStack stack);
    void onRemoved(LivingEntity entityLivingBase, ItemStack stack);

    interface ISidedLimb
    {
        EnumSide getSide(ItemStack stack);

        enum EnumSide
        {
            LEFT,
            RIGHT;
        }
    }

    int getEssenceCost(ItemStack stack);
}