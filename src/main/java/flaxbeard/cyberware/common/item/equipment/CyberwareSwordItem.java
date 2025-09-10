package flaxbeard.cyberware.common.item.equipment;

import flaxbeard.cyberware.api.item.IDeconstructable;
import flaxbeard.cyberware.common.item.CreativeModeTabs;
import net.minecraft.item.IItemTier;
import net.minecraft.item.SwordItem;

public class CyberwareSwordItem extends SwordItem implements IDeconstructable {
    public CyberwareSwordItem(IItemTier itemTier, int damageModifier, float attackSpeed, Properties properties) {
        super(itemTier, damageModifier, attackSpeed, properties.tab(CreativeModeTabs.EQUIPMENT_GROUP));
    }
}
