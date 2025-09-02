package flaxbeard.cyberware.common.item.equipment;

import flaxbeard.cyberware.api.item.IDeconstructable;
import flaxbeard.cyberware.common.item.CreativeModeTabs;
import flaxbeard.cyberware.common.item.CyberwareItems;
import flaxbeard.cyberware.common.misc.NNLUtil;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;

public class CyberwareSwordItem extends SwordItem implements IDeconstructable {
    public CyberwareSwordItem(IItemTier itemTier, int damageModifier, float attackSpeed, Properties properties) {
        super(itemTier, damageModifier, attackSpeed, properties.tab(CreativeModeTabs.EQUIPMENT_GROUP));
    }

    @Override
    public boolean canDestroy(ItemStack stack) {
        return true;
    }

    @Override
    @Nonnull
    public NonNullList<ItemStack> getComponents(ItemStack stack) {
        return NNLUtil.fromArray(new ItemStack[] {
                new ItemStack(Items.IRON_INGOT, 2),
                new ItemStack(CyberwareItems.COMPONENT.get(2).get(), 1),
                new ItemStack(CyberwareItems.COMPONENT.get(4).get(), 1)
        });
    }
}
