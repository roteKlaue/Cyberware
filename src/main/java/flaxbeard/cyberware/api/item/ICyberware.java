package flaxbeard.cyberware.api.item;

import lombok.Getter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.ArrayList;
import java.util.List;

public interface ICyberware {
    BodySlot getSlot();
    int installedStackSize(ItemStack stack);
    NonNullList<NonNullList<ItemStack>> required();
    default boolean isIncompatible(ItemStack other) { return false; }
    boolean isEssential();
    int getCapacity(ItemStack wareStack);

    enum BodySlot {
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

        @Getter
        private final int slotNumber;
        @Getter
        private final String name;
        private final boolean sidedSlot;
        private final boolean hasEssential;

        BodySlot(int slot, String name, boolean sidedSlot, boolean hasEssential) {
            this.slotNumber = slot;
            this.name = name;
            this.sidedSlot = sidedSlot;
            this.hasEssential = hasEssential;
        }

        BodySlot(int slot, String name) {
            this(slot, name, false, true);
        }

        public static BodySlot getSlotByPage(int page) {
            for (BodySlot slot : values()) {
                if (slot.getSlotNumber() == page) {
                    return slot;
                }
            }
            return null;
        }

        public boolean isSided() {
            return sidedSlot;
        }

        public boolean hasEssential() {
            return hasEssential;
        }
    }

    void onAdded(LivingEntity livingEntity, ItemStack stack);
    void onRemoved(LivingEntity livingEntity, ItemStack stack);

    interface ISidedLimb {
        EnumSide getSide(ItemStack stack);

        enum EnumSide {
            LEFT,
            RIGHT
        }
    }

    int getEssenceCost(ItemStack stack);
}