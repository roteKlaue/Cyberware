package flaxbeard.cyberware.common.handler;

import flaxbeard.cyberware.api.CyberwareUserDataProvider;
import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.common.entity.CyberZombieEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = OverclockedOrgans.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CapabilityEventHandler {
    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity
            || event.getObject() instanceof CyberZombieEntity) {
            event.addCapability(CyberwareUserDataProvider.NAME, new CyberwareUserDataProvider());
        }
    }
}
