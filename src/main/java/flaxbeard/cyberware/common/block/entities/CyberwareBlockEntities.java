package flaxbeard.cyberware.common.block.entities;

import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.common.block.CyberwareBlocks;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.function.Supplier;

public class CyberwareBlockEntities {
    public static final DeferredRegister<TileEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, OverclockedOrgans.MOD_ID);

    public static final RegistryObject<TileEntityType<ComponentBoxBlockEntity>> COMPONENT_BOX =
            registerBlockEntity("component_box", ComponentBoxBlockEntity::new, CyberwareBlocks.COMPONENT_BOX_BLOCK);

    public static final RegistryObject<TileEntityType<BlueprintArchiveBlockEntity>> BLUEPRINT_ARCHIVE = registerBlockEntity("blueprint_archive",
            BlueprintArchiveBlockEntity::new, CyberwareBlocks.BLUEPRINT_ARCHIVE_BLOCK);

    public static final RegistryObject<TileEntityType<EngineeringTableBlockEntity>> ENGINEERING_TABLE =
            registerBlockEntity("engineering_table", EngineeringTableBlockEntity::new, CyberwareBlocks.ENGINEERING_TABLE_BLOCK);

    public static final RegistryObject<TileEntityType<SurgeryChamberBlockEntity>> SURGERY_CHAMBER =
            registerBlockEntity("surgery_chamber", SurgeryChamberBlockEntity::new, CyberwareBlocks.SURGERY_CHAMBER_BLOCK);

    public static final RegistryObject<TileEntityType<ScannerBlockEntity>> SCANNER =
            registerBlockEntity("scanner", ScannerBlockEntity::new, CyberwareBlocks.SCANNER_BLOCK);

    public static final RegistryObject<TileEntityType<SurgeryBlockEntity>> SURGERY =
            registerBlockEntity("surgery", SurgeryBlockEntity::new, CyberwareBlocks.SURGERY_BLOCK);

    public static final RegistryObject<TileEntityType<ChargerBlockEntity>> CHARGER =
            registerBlockEntity("charger", ChargerBlockEntity::new, CyberwareBlocks.CHARGER_BLOCK);

    public static <T extends TileEntity, B extends Block>
    RegistryObject<TileEntityType<T>> registerBlockEntity(
            String name,
            Supplier<T> supplier,
            CyberwareBlocks.RegisteredBlock<B> item
    ) {
        return BLOCK_ENTITIES.register(
                name.toLowerCase(),
                () -> TileEntityType.Builder.of(supplier, item.getBlock().get()).build(null)
        );
    }

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
