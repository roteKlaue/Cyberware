package flaxbeard.cyberware.common.attributes;

import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.common.CyberwareConfig;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CyberwareAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(ForgeRegistries.ATTRIBUTES, OverclockedOrgans.MOD_ID);

    public static final RegistryObject<Attribute> TOLERANCE = ATTRIBUTES.register(
            "essence",
            () -> new RangedAttribute("attribute.overclockedorgans.tolerance",
                    CyberwareConfig.ESSENCE.get(),
                    0.0F, Double.MAX_VALUE
            )
    );

    public static void register(IEventBus bus) {
        ATTRIBUTES.register(bus);
    }
}
