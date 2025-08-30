package flaxbeard.cyberware;

import flaxbeard.cyberware.client.gui.CyberwareContainers;
import flaxbeard.cyberware.common.CyberwareConfig;
import flaxbeard.cyberware.common.block.CyberwareBlocks;
import flaxbeard.cyberware.common.block.entities.CyberwareBlockEntities;
import flaxbeard.cyberware.common.item.CyberwareItems;
import flaxbeard.cyberware.common.misc.CyberwareRecipeSerializers;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
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

import java.util.stream.Collectors;


// The value here should match an entry in the META-INF/mods.toml file
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

        eventBus.addListener(this::setup);
        eventBus.addListener(this::enqueueIMC);
        eventBus.addListener(this::processIMC);
        eventBus.addListener(this::doClientStuff);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        CyberwareContainers.initScreens();
        registerItemProperties();
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        InterModComms.sendTo(MOD_ID, "helloworld", () -> {
            LOGGER.info("Hello world from the MDK");
            return "Hello world";
        });
    }

    private void processIMC(final InterModProcessEvent event) {
        LOGGER.info("Got IMC {}", event.getIMCStream().map(m -> m.getMessageSupplier().get()).collect(Collectors.toList()));
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            LOGGER.info("HELLO from Register Block");
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void registerItemProperties() {
        ItemModelsProperties.register(
                CyberwareItems.BLUEPRINT.get(),
                new ResourceLocation("blueprint_type"),
                (stack, world, entity) -> {
                    if (stack.hasTag() && stack.getTag() != null && stack.getTag().contains("BlueprintId")) {
                        String id = stack.getTag().getString("BlueprintId");
                        return id.equals("empty") ? 1f : 0f;
                    }
                    return 0f;
                }
        );
    }

}
