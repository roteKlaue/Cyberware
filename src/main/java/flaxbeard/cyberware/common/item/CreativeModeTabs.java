package flaxbeard.cyberware.common.item;

import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.common.CyberwareConfig;
import flaxbeard.cyberware.common.block.CyberwareBlocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class CreativeModeTabs {
    public static final ItemGroup MANUFACTURED_GROUP = new ItemGroup(OverclockedOrgans.MOD_ID + ".manufacturedModTab")
    {
        @Override
        @Nonnull
        public ItemStack makeIcon()
        {
            return new ItemStack(CyberwareItems.CYBER_EYES_MANUFACTURED.get());
        }
    };

    public static final ItemGroup SALVAGED_GROUP = new ItemGroup(OverclockedOrgans.MOD_ID + ".salvagedModTab")
    {
        @Override
        @Nonnull
        public ItemStack makeIcon() {
            return new ItemStack(CyberwareItems.CYBER_EYES_SALVAGED.get());
        }
    };

    public static ItemGroup EQUIPMENT_GROUP;

    public static final ItemGroup BLOCK_GROUP = new ItemGroup(OverclockedOrgans.MOD_ID + ".blockModTab")
    {
        @Override
        @Nonnull
        public ItemStack makeIcon()
        {
            return new ItemStack(CyberwareBlocks.COMPONENT_BOX_BLOCK.getFirst().get());
        }
    };

    static {
        boolean katanaEnabled = CyberwareConfig.ENABLE_KATANA.get();
        if (katanaEnabled || CyberwareConfig.ENABLE_CLOTHES.get()) {
            EQUIPMENT_GROUP = new ItemGroup(OverclockedOrgans.MOD_ID + ".equipmentModTab")
            {
                @Override
                @Nonnull
                public ItemStack makeIcon()
                {
                    return new ItemStack(katanaEnabled
                            ? CyberwareItems.KATANA.get()
                            : CyberwareItems.JACKET.get());
                }
            };
        }
    }
}
