package flaxbeard.cyberware.common.misc.recipe;

import com.google.gson.JsonObject;
import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.common.CyberwareConfig;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class ConfigEnabledCondition implements ICondition {
    private static final ResourceLocation NAME = new ResourceLocation(OverclockedOrgans.MOD_ID, "config_enabled");
    private final String key;

    public ConfigEnabledCondition(String key) {
        this.key = key;
    }

    @Override
    public ResourceLocation getID() {
        return new ResourceLocation(OverclockedOrgans.MOD_ID, "config_enabled");
    }

    @Override
    public boolean test() {
        if ("surgery_crafting".equals(key)) {
            return CyberwareConfig.SURGERY_CRAFTING.get();
        }
        return false;
    }

    public static class Serializer implements IConditionSerializer<ConfigEnabledCondition> {
        @Override
        public void write(JsonObject json, ConfigEnabledCondition value) {
            json.addProperty("key", value.key);
        }

        @Override
        public ConfigEnabledCondition read(JsonObject json) {
            return new ConfigEnabledCondition(json.get("key").getAsString());
        }

        @Override
        public ResourceLocation getID() {
            return NAME;
        }
    }
}
