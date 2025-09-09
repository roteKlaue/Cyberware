package flaxbeard.cyberware;

import flaxbeard.cyberware.client.gui.CyberwareContainers;
import flaxbeard.cyberware.common.CyberwareConfig;
import flaxbeard.cyberware.common.block.CyberwareBlocks;
import flaxbeard.cyberware.common.block.entities.CyberwareBlockEntities;
import flaxbeard.cyberware.common.effect.CyberwarePotionEffects;
import flaxbeard.cyberware.common.entity.CyberwareEntities;
import flaxbeard.cyberware.common.item.CyberwareItems;
import flaxbeard.cyberware.common.misc.CyberwareRecipeSerializers;
import flaxbeard.cyberware.common.network.CyberwarePackets;
import net.minecraft.block.Block;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(OverclockedOrgans.MOD_ID)
public class OverclockedOrgans {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "overclockedorgans";

    public OverclockedOrgans() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CyberwareConfig.COMMON_CONFIG);

        CyberwareItems.register(eventBus);
        CyberwareBlocks.register(eventBus);
        CyberwareBlockEntities.register(eventBus);
        CyberwareContainers.register(eventBus);
        CyberwareRecipeSerializers.register(eventBus);
        CyberwarePotionEffects.register(eventBus);
        CyberwareEntities.register(eventBus);

        eventBus.addListener(this::setup);
        eventBus.addListener(this::enqueueIMC);
        eventBus.addListener(this::processIMC);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        CyberwarePackets.register();
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {}

    private void processIMC(final InterModProcessEvent event) {}

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {}

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {}
    }
}
