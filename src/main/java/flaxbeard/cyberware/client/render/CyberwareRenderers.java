package flaxbeard.cyberware.client.render;

import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.common.block.entities.CyberwareBlockEntities;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = OverclockedOrgans.MOD_ID,  bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CyberwareRenderers {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        ClientRegistry.bindTileEntityRenderer(
                CyberwareBlockEntities.SURGERY_CHAMBER.get(),
                SurgeryChamberRenderer::new
        );

        ClientRegistry.bindTileEntityRenderer(
                CyberwareBlockEntities.SCANNER.get(),
                ScannerRenderer::new
        );
    }
}
