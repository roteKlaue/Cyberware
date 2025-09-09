package flaxbeard.cyberware.common.item.equipment;

import flaxbeard.cyberware.api.item.IDeconstructable;
import flaxbeard.cyberware.common.item.CreativeModeTabs;
import flaxbeard.cyberware.common.item.CyberwareItems;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.block.Blocks;
import net.minecraft.util.NonNullList;

public class CyberwareArmorItem extends ArmorItem implements IDeconstructable {
    public CyberwareArmorItem(IArmorMaterial armorMaterial,
                              EquipmentSlotType equipmentSlot,
                              Properties properties) {
        super(armorMaterial, equipmentSlot, properties.tab(CreativeModeTabs.EQUIPMENT_GROUP));
    }

    @Override
    public boolean canDestroy(ItemStack stack) {
        return true;
    }

    @Override
    public NonNullList<ItemStack> getComponents(ItemStack stack) {
        Item item = stack.getItem();

        NonNullList<ItemStack> nnl = NonNullList.create();

        if (item == CyberwareItems.TRENCHCOAT.get()) {
            nnl.add(new ItemStack(CyberwareItems.COMPONENTS.get(2).get(), 2));
            nnl.add(new ItemStack(Items.LEATHER, 12));
            nnl.add(new ItemStack(Items.BLACK_DYE, 1));
        } else if (item == CyberwareItems.JACKET.get()) {
            nnl.add(new ItemStack(CyberwareItems.COMPONENTS.get(2).get(), 1));
            nnl.add(new ItemStack(Items.LEATHER, 8));
            nnl.add(new ItemStack(Items.BLACK_DYE, 1));
        } else {
            nnl.add(new ItemStack(Blocks.BLACK_STAINED_GLASS, 4));
            nnl.add(new ItemStack(CyberwareItems.COMPONENTS.get(4).get(), 1));
        }

        return nnl;
    }
}
