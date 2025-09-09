package flaxbeard.cyberware.common.block;

import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.common.block.items.ComponentBoxItem;
import flaxbeard.cyberware.common.item.CyberwareItems;
import lombok.Data;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
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

    public static final RegisteredBlock<Block> TEST_BLOCK = registerBlock("test_block",
            AbstractBlock.Properties.copy(Blocks.IRON_BLOCK));

    public static final RegisteredBlock<ComponentBoxBlock> COMPONENT_BOX_BLOCK = registerBlock("component_box", ComponentBoxBlock::new, ComponentBoxItem::new);
    public static final RegisteredBlock<BlueprintArchiveBlock> BLUEPRINT_ARCHIVE_BLOCK = registerBlock("blueprint_archive", BlueprintArchiveBlock::new);
    public static final RegisteredBlock<EngineeringTableBlock> ENGINEERING_TABLE_BLOCK = registerBlock("engineering_table", EngineeringTableBlock::new);
    public static final RegisteredBlock<SurgeryChamberBlock> SURGERY_CHAMBER_BLOCK = registerBlock("surgery_chamber", SurgeryChamberBlock::new);
    public static final RegisteredBlock<ChargerBlock> CHARGER_BLOCK = registerBlock("charger", ChargerBlock::new);
    public static final RegisteredBlock<SurgeryBlock> SURGERY_BLOCK = registerBlock("surgery", SurgeryBlock::new);
    public static final RegisteredBlock<ScannerBlock> SCANNER_BLOCK = registerBlock("scanner", ScannerBlock::new);

    public static <T extends Block> RegisteredBlock<T> registerBlock(String id, Supplier<T> supplier) {
        return registerBlock(id, supplier, BlockItem::new);
    }

    public static <T extends Block> RegisteredBlock<T> registerBlock(String id, Supplier<T> supplier, BiFunction<Block, Item.Properties, ? extends BlockItem> itemSupplier) {
        RegistryObject<T> block = BLOCKS.register(id.toLowerCase(), supplier);
        return new RegisteredBlock<>(CyberwareItems.registerBlockItem(id, block, itemSupplier), block);
    }

    public static RegisteredBlock<Block> registerBlock(String id, AbstractBlock.Properties properties) {
        return registerBlock(id, () -> new Block(properties));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

    @Data
    public static class RegisteredBlock<T extends Block> {
        private final RegistryObject<BlockItem> item;
        private final RegistryObject<T> block;

        public RegisteredBlock(RegistryObject<BlockItem> item, RegistryObject<T> block) {
            this.item = item;
            this.block = block;
        }

        public Supplier<Item> asItemSupplier() {
            return item::get;
        }
    }
}
