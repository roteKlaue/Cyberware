package flaxbeard.cyberware.client;

import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.client.gui.CyberwareContainers;
import flaxbeard.cyberware.client.render.entity.CyberZombieRenderer;
import flaxbeard.cyberware.common.entity.CyberwareEntities;
import flaxbeard.cyberware.common.item.CyberwareItems;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.event.TickEvent;

@Mod.EventBusSubscriber(modid = OverclockedOrgans.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {
    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        registerItemProperties();
        CyberwareContainers.initScreens();
        KeyBinds.init();
        RenderingRegistry.registerEntityRenderingHandler(CyberwareEntities.CYBER_ZOMBIE.get(), CyberZombieRenderer::new);
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
