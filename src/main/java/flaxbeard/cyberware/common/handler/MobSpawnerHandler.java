package flaxbeard.cyberware.common.handler;

import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.common.CyberwareConfig;
import flaxbeard.cyberware.common.entity.CyberwareEntities;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = OverclockedOrgans.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class MobSpawnerHandler {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntitySpawn(BiomeLoadingEvent event) {
        if (event.getSpawns().getSpawner(EntityClassification.MONSTER) == null) return;
        if (event.getSpawns().getSpawner(EntityClassification.MONSTER).stream().anyMatch(e -> e.type.equals(EntityType.ZOMBIE))) return;
        if (event.getCategory().equals(Biome.Category.NETHER) || event.getCategory().equals(Biome.Category.THEEND)) return;
        if (!CyberwareConfig.MOBS_ENABLE_CYBER_ZOMBIES.get()) return;
        event.getSpawns().addSpawn(
                EntityClassification.MONSTER,
                new MobSpawnInfo.Spawners(
                        CyberwareEntities.CYBER_ZOMBIE.get(),
                        CyberwareConfig.MOBS_CYBER_ZOMBIE_WEIGHT.get(),
                        CyberwareConfig.MOBS_CYBER_ZOMBIE_MIN_PACK.get(),
                        CyberwareConfig.MOBS_CYBER_ZOMBIE_MAX_PACK.get()
                )
        );
    }
}
