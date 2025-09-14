package flaxbeard.cyberware.client;

import flaxbeard.cyberware.OverclockedOrgans;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = OverclockedOrgans.MOD_ID, value = Dist.CLIENT)
public class ClientTickHandler {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (KeyBinds.menu.isDown()) {
                // open your GUI here
            }
        }
    }
}
