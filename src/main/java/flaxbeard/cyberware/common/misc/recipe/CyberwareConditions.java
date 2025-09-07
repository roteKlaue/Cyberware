package flaxbeard.cyberware.common.misc.recipe;

import flaxbeard.cyberware.OverclockedOrgans;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = OverclockedOrgans.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CyberwareConditions {
    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        // CraftingHelper.register(new ConfigEnabledCondition.Serializer());
    }
}
