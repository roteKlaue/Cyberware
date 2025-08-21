package flaxbeard.cyberware.common.block.entities;

import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.common.block.CyberwareBlocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CyberwareBlockEntities {
    public static final DeferredRegister<TileEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, OverclockedOrgans.MOD_ID);

    public static final RegistryObject<TileEntityType<ComponentBoxBlockEntity>> COMPONENT_BOX =
            BLOCK_ENTITIES.register("component_box",
                    () -> TileEntityType.Builder.of(ComponentBoxBlockEntity::new, CyberwareBlocks.COMPONENT_BOX_BLOCK.getSecond().get())
                    .build(null));

    public static final RegistryObject<TileEntityType<BlueprintArchiveBlockEntity>> BLUEPRINT_ARCHIVE =
            BLOCK_ENTITIES.register("blueprint_archive",
                    () -> TileEntityType.Builder.of(BlueprintArchiveBlockEntity::new, CyberwareBlocks.BLUEPRINT_ARCHIVE_BLOCK.getSecond().get())
                            .build(null));

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
