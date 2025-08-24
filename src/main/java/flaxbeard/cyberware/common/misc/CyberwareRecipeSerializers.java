package flaxbeard.cyberware.common.misc;

import flaxbeard.cyberware.OverclockedOrgans;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CyberwareRecipeSerializers {
    public static final DeferredRegister<IRecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, OverclockedOrgans.MOD_ID);

    public static void register(IEventBus bus) {
        SERIALIZERS.register(bus);
    }
}
