package flaxbeard.cyberware.common.item.equipment;

import flaxbeard.cyberware.api.item.IDeconstructable;
import flaxbeard.cyberware.common.item.CreativeModeTabs;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;

public class CyberwareArmorItem extends ArmorItem implements IDeconstructable {
    public CyberwareArmorItem(IArmorMaterial armorMaterial,
                              EquipmentSlotType equipmentSlot,
                              Properties properties) {
        super(armorMaterial, equipmentSlot, properties.tab(CreativeModeTabs.EQUIPMENT_GROUP));
    }
}
