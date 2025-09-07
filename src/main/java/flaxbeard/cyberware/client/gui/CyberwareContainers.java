package flaxbeard.cyberware.client.gui;

import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.common.block.entities.BlueprintArchiveBlockEntity;
import flaxbeard.cyberware.common.block.entities.EngineeringTableBlockEntity;
import flaxbeard.cyberware.common.block.entities.ScannerBlockEntity;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CyberwareContainers {
    public static final DeferredRegister<ContainerType<?>> CONTAINERS =
            DeferredRegister.create(ForgeRegistries.CONTAINERS, OverclockedOrgans.MOD_ID);

    public static final RegistryObject<ContainerType<ComponentBoxContainer>> COMPONENT_BOX =
            CONTAINERS.register("component_box", () -> IForgeContainerType.create(ComponentBoxContainer::new));

    public static final RegistryObject<ContainerType<BlueprintArchiveContainer>> BLUEPRINT_ARCHIVE =
            CONTAINERS.register("blueprint_archive",
                    () -> IForgeContainerType.create((windowId, inv, data) -> {
                        BlockPos pos = data.readBlockPos();
                        TileEntity tile = inv.player.level.getBlockEntity(pos);
                        if (tile instanceof BlueprintArchiveBlockEntity) {
                            return new BlueprintArchiveContainer(windowId, inv, (BlueprintArchiveBlockEntity) tile);
                        }
                        return null;
                    }));

    public static final RegistryObject<ContainerType<ScannerContainer>> SCANNER =
            CONTAINERS.register("scanner",
                    () -> IForgeContainerType.create((windowId, inv, data) -> {
                        BlockPos pos = data.readBlockPos();
                        TileEntity tile = inv.player.level.getBlockEntity(pos);
                        if (tile instanceof ScannerBlockEntity) {
                            return new ScannerContainer(windowId, inv, (ScannerBlockEntity) tile);
                        }
                        return null;
                    }));

    public static final RegistryObject<ContainerType<EngineeringTableContainer>> ENGINEERING =
            CONTAINERS.register("engineering",
                    () -> IForgeContainerType.create((windowId, inv, data) -> {
                        BlockPos pos = data.readBlockPos();
                        TileEntity tile = inv.player.level.getBlockEntity(pos);
                        if (tile instanceof EngineeringTableBlockEntity) {
                            return new EngineeringTableContainer(windowId, inv, (EngineeringTableBlockEntity) tile);
                        }
                        return null;
                    }));


    public static void register(IEventBus bus) {
        CONTAINERS.register(bus);
    }

    @OnlyIn(Dist.CLIENT)
    public static void initScreens() {
        ScreenManager.register(CyberwareContainers.COMPONENT_BOX.get(), ComponentBoxScreen::new);
        ScreenManager.register(CyberwareContainers.BLUEPRINT_ARCHIVE.get(), BlueprintArchiveScreen::new);
        ScreenManager.register(CyberwareContainers.SCANNER.get(), ScannerScreen::new);
        ScreenManager.register(CyberwareContainers.ENGINEERING.get(), EngineeringTableScreen::new);
    }
}
