package flaxbeard.cyberware.common.effect;

import flaxbeard.cyberware.OverclockedOrgans;
import net.minecraft.potion.Effect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CyberwarePotionEffects {
    public static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, OverclockedOrgans.MOD_ID);

    public static final RegistryObject<Effect> NEUROPOZYNE = EFFECTS.register("neuropozyne",
            NeuropozynePotion::new);

    public static void register(IEventBus event) {
        EFFECTS.register(event);
    }
}
