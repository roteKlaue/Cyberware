package flaxbeard.cyberware.common.item;

import flaxbeard.cyberware.OverclockedOrgans;
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

    public static final ItemGroup EQUIPMENT_GROUP = new ItemGroup(OverclockedOrgans.MOD_ID + ".equipmentModTab")
    {
        @Override
        @Nonnull
        public ItemStack makeIcon()
        {
            return new ItemStack(CyberwareItems.KATANA.get());
        }
    };

    public static final ItemGroup BLOCK_GROUP = new ItemGroup(OverclockedOrgans.MOD_ID + ".blockModTab")
    {
        @Override
        @Nonnull
        public ItemStack makeIcon()
        {
            return new ItemStack(CyberwareBlocks.COMPONENT_BOX_BLOCK.getFirst().get());
        }
    };
}
