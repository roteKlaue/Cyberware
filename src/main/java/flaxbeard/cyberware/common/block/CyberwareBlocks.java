package flaxbeard.cyberware.common.block;

import com.mojang.datafixers.util.Pair;
import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.common.block.items.ComponentBoxItem;
import flaxbeard.cyberware.common.item.CyberwareItems;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class CyberwareBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, OverclockedOrgans.MOD_ID);

    public static final Pair<RegistryObject<BlockItem>, RegistryObject<Block>> TEST_BLOCK = registerBlock("test_block",
            AbstractBlock.Properties.of(Material.METAL));

    public static final Pair<RegistryObject<BlockItem>, RegistryObject<ComponentBoxBlock>> COMPONENT_BOX_BLOCK = registerBlock("component_box", ComponentBoxBlock::new, ComponentBoxItem::new);

    public static final Pair<RegistryObject<BlockItem>, RegistryObject<BlueprintArchiveBlock>> BLUEPRINT_ARCHIVE_BLOCK = registerBlock("blueprint_archive", BlueprintArchiveBlock::new);

    public static final Pair<RegistryObject<BlockItem>, RegistryObject<ChargerBlock>> CHARGER_BLOCK = registerBlock("charger", ChargerBlock::new);

    public static <T extends Block> Pair<RegistryObject<BlockItem>, RegistryObject<T>> registerBlock(String id, Supplier<T> supplier) {
        RegistryObject<T> block = BLOCKS.register(id.toLowerCase(), supplier);
        return new Pair<>(CyberwareItems.registerBlockItem(id.toLowerCase(), block), block);
    }

    public static <T extends Block> Pair<RegistryObject<BlockItem>, RegistryObject<T>> registerBlock(String id, Supplier<T> supplier, BiFunction<Block, Item.Properties, ? extends BlockItem> itemSupplier) {
        RegistryObject<T> block = BLOCKS.register(id.toLowerCase(), supplier);
        return new Pair<>(CyberwareItems.registerBlockItem(id.toLowerCase(), block, itemSupplier), block);
    }

    public static Pair<RegistryObject<BlockItem>, RegistryObject<Block>> registerBlock(String id, AbstractBlock.Properties properties) {
        return registerBlock(id, () -> new Block(properties));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
