package flaxbeard.cyberware.common.item;

import flaxbeard.cyberware.api.item.IDeconstructable;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class TestCyberwareItem extends CyberwareBaseItem implements IDeconstructable {
    @Override
    public boolean canDestroy(ItemStack stack) {
        return true;
    }

    @Override
    public NonNullList<ItemStack> getComponents(ItemStack stack) {
        return null;
    }
}
