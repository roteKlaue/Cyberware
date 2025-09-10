package flaxbeard.cyberware.common.misc;

import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.common.misc.recipe.DestructingRecipe;
import flaxbeard.cyberware.common.misc.recipe.EngineeringRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class CyberwareRecipeSerializers {
    public static final DeferredRegister<IRecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, OverclockedOrgans.MOD_ID);

    public static final RegistryObject<IRecipeSerializer<?>> ENGINEERING =
            SERIALIZERS.register(EngineeringRecipe.Type.ID, () -> EngineeringRecipe.Serializer.INSTANCE);

    public static final RegistryObject<IRecipeSerializer<?>> DESTRUCTING =
            SERIALIZERS.register(DestructingRecipe.Type.ID, () -> DestructingRecipe.Serializer.INSTANCE);

    public static void register(IEventBus bus) {
        SERIALIZERS.register(bus);
    }
}
