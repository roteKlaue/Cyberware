package flaxbeard.cyberware.common.block.entities;

import com.mojang.datafixers.util.Pair;
import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.common.block.CyberwareBlocks;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
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

    public static <T extends TileEntity, B extends Block>
    RegistryObject<TileEntityType<T>> registerBlockEntity(
            String name,
            Supplier<T> supplier,
            Pair<RegistryObject<BlockItem>, RegistryObject<B>> item
    ) {
        return BLOCK_ENTITIES.register(
                name.toLowerCase(),
                () -> TileEntityType.Builder.of(supplier, item.getSecond().get()).build(null)
        );
    }

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
