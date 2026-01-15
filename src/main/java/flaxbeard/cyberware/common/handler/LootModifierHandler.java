package flaxbeard.cyberware.common.handler;

import com.google.gson.JsonObject;
import flaxbeard.cyberware.OverclockedOrgans;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.List;

@Mod.EventBusSubscriber(modid = OverclockedOrgans.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class LootModifierHandler {
    public static class SurgeryBlockEntryModifier extends LootModifier {
        private final Item item;

        public SurgeryBlockEntryModifier(ILootCondition[] conditionsIn, Item item) {
            super(conditionsIn);
            this.item = item;
        }

        @Nonnull
        @Override
        protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
            if (context.getRandom().nextFloat() > 0.4) {
                generatedLoot.add(new ItemStack(item, 1));
            }
            return generatedLoot;
        }

        public static class Serializer extends GlobalLootModifierSerializer<SurgeryBlockEntryModifier> {
            @Override
            public SurgeryBlockEntryModifier read(ResourceLocation name, JsonObject object, ILootCondition[] conditionsIn) {
                Item addition = ForgeRegistries.ITEMS.getValue(new ResourceLocation(object.get("addition").getAsString()));
                return new SurgeryBlockEntryModifier(conditionsIn, addition);
            }

            @Override
            public JsonObject write(SurgeryBlockEntryModifier instance) {
                JsonObject json = makeConditions(instance.conditions);
                json.addProperty("addition", ForgeRegistries.ITEMS.getKey(instance.item).toString());
                return json;
            }
        }
    }

    @SubscribeEvent
    public static void registerModifierSerializers(@Nonnull final RegistryEvent.Register<GlobalLootModifierSerializer<?>> event) {
        event.getRegistry().registerAll(
                new SurgeryBlockEntryModifier.Serializer().setRegistryName(new ResourceLocation(OverclockedOrgans.MOD_ID,"surgery_chamber"))
        );
    }
}
