package flaxbeard.cyberware.client.gui;

import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.common.block.entities.ComponentBoxBlockEntity;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CyberwareContainers {
    public static final DeferredRegister<ContainerType<?>> CONTAINERS =
            DeferredRegister.create(ForgeRegistries.CONTAINERS, OverclockedOrgans.MOD_ID);

    public static final RegistryObject<ContainerType<ComponentBoxContainer>> COMPONENT_BOX =
            CONTAINERS.register("component_box", () -> IForgeContainerType.create(ComponentBoxContainer::new));


    public static void register(IEventBus bus) {
        CONTAINERS.register(bus);
    }

    public static void initClient(final FMLClientSetupEvent event) {
        ScreenManager.register(CyberwareContainers.COMPONENT_BOX.get(), ComponentBoxScreen::new);
    }
}
