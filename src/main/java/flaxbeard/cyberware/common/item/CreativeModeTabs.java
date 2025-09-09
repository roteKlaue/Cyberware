package flaxbeard.cyberware.common.item;

import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.common.CyberwareConfig;
import flaxbeard.cyberware.common.block.CyberwareBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class CreativeModeTabs {
    private static class CyberwareItemGroup extends ItemGroup   {
        private final Supplier<Item> icon;
        public CyberwareItemGroup(String label, Supplier<Item> icon) {
            super(OverclockedOrgans.MOD_ID + "." + label);
            this.icon = icon;
        }
        @Override
        @Nonnull
        public ItemStack makeIcon()
        {
            return new ItemStack(icon.get());
        }
    }

    public static final ItemGroup MANUFACTURED_GROUP = new CyberwareItemGroup("manufacturedModTab", CyberwareItems.CYBER_EYES_MANUFACTURED::get);
    public static final ItemGroup SALVAGED_GROUP = new CyberwareItemGroup("salvagedModTab", CyberwareItems.CYBER_EYES_SALVAGED::get);
    public static ItemGroup EQUIPMENT_GROUP;
    public static final ItemGroup BLOCK_GROUP = new CyberwareItemGroup("blockModTab", CyberwareBlocks.COMPONENT_BOX_BLOCK.asItemSupplier());

    static {
        boolean katanaEnabled = CyberwareConfig.ENABLE_KATANA.get();
        if (katanaEnabled || CyberwareConfig.ENABLE_CLOTHES.get()) {
            EQUIPMENT_GROUP = new CyberwareItemGroup("equipmentModTab", katanaEnabled
                    ? CyberwareItems.KATANA::get
                    : CyberwareItems.JACKET::get);
        }
    }
}
