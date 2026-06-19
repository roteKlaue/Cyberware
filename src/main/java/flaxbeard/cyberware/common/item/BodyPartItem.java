package flaxbeard.cyberware.common.item;

import flaxbeard.cyberware.api.CyberwareAPI;
import flaxbeard.cyberware.api.item.ICyberware;
import lombok.Getter;
import net.minecraft.item.ItemStack;

@Getter
public class BodyPartItem extends CyberwareItem implements ICyberware.ISidedLimb {
    private final Variant variant;

    public BodyPartItem(Variant variant) {
        super(
                new CyberwareProperties()
                        .slot(variant.slot)
                        .essence(0)
                        .essential()
        );

        this.variant = variant;
    }

    @Override
    public boolean isIncompatible(ItemStack other) {
        if (!CyberwareAPI.isCyberware(other)) return false;

        ICyberware ware = CyberwareAPI.getCyberware(other);

        if (!ware.isEssential()) return false;

        if (!variant.isSided()) return true;

        if (!(ware instanceof ICyberware.ISidedLimb)) return false;

        return ((ICyberware.ISidedLimb) ware).getSide(other) == getSide(ItemStack.EMPTY);
    }

    @Override
    public EnumSide getSide(ItemStack stack) {
        return variant.side;
    }

    public enum Variant {
        EYES(ICyberware.BodySlot.EYES, null),
        BRAIN(ICyberware.BodySlot.CRANIUM, null),
        HEART(ICyberware.BodySlot.HEART, null),
        LUNGS(ICyberware.BodySlot.LUNGS, null),
        STOMACH(ICyberware.BodySlot.LOWER_ORGANS, null),
        SKIN(ICyberware.BodySlot.SKIN, null),
        MUSCLES(ICyberware.BodySlot.MUSCLE, null),
        BONES(ICyberware.BodySlot.BONE, null),

        ARM_LEFT(ICyberware.BodySlot.ARM, EnumSide.LEFT),
        ARM_RIGHT(ICyberware.BodySlot.ARM, EnumSide.RIGHT),
        LEG_LEFT(ICyberware.BodySlot.LEG, EnumSide.LEFT),
        LEG_RIGHT(ICyberware.BodySlot.LEG, EnumSide.RIGHT);

        private final ICyberware.BodySlot slot;
        private final EnumSide side;

        Variant(ICyberware.BodySlot slot, EnumSide side) {
            this.slot = slot;
            this.side = side;
        }

        public boolean isSided() {
            return side != null;
        }
    }
}