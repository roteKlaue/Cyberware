package flaxbeard.cyberware.common.entity;

import flaxbeard.cyberware.OverclockedOrgans;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = OverclockedOrgans.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CyberwareEntities {
    public static DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, OverclockedOrgans.MOD_ID);

    public static RegistryObject<EntityType<CyberZombieEntity>> CYBER_ZOMBIE =
            ENTITIES.register("cyber_zombie",
                    () -> EntityType.Builder
                            .of(CyberZombieEntity::new, EntityClassification.MONSTER)
                            .sized(0.6F, 1.95F)
                            .clientTrackingRange(8)
                            .build(new ResourceLocation(OverclockedOrgans.MOD_ID, "cyber_zombie").toString())
            );

    public static void register(IEventBus bus) {
        ENTITIES.register(bus);
    }

    @SubscribeEvent
    public static void onRegisterAttributes(RegistryEvent.Register<EntityType<?>> event) {
        GlobalEntityTypeAttributes.put(CYBER_ZOMBIE.get(), CyberZombieEntity.createAttributes().build());
    }
}
