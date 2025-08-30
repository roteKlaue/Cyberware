package flaxbeard.cyberware.common;

import flaxbeard.cyberware.OverclockedOrgans;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = OverclockedOrgans.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CyberwareConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static ForgeConfigSpec.DoubleValue ENGINEERING_CHANCE;
    public static ForgeConfigSpec.DoubleValue SCANNER_CHANCE;
    public static ForgeConfigSpec.DoubleValue SCANNER_CHANCE_ADDL;
    public static ForgeConfigSpec.IntValue SCANNER_TIME;
    public static ForgeConfigSpec.IntValue TESLA_PER_POWER;

    public static ForgeConfigSpec.IntValue ESSENCE;
    public static ForgeConfigSpec.IntValue CRITICAL_ESSENCE;

    public static ForgeConfigSpec.BooleanValue MOBS_ENABLE_CYBER_ZOMBIES;
    public static ForgeConfigSpec.IntValue MOBS_CYBER_ZOMBIE_WEIGHT;
    public static ForgeConfigSpec.IntValue MOBS_CYBER_ZOMBIE_MIN_PACK;
    public static ForgeConfigSpec.IntValue MOBS_CYBER_ZOMBIE_MAX_PACK;
    public static ForgeConfigSpec.BooleanValue MOBS_IS_DIMENSION_BLACKLIST;
    public static ForgeConfigSpec.BooleanValue MOBS_APPLY_DIMENSION_TO_SPAWNING;
    public static ForgeConfigSpec.BooleanValue MOBS_APPLY_DIMENSION_TO_BEACON;
    public static ForgeConfigSpec.BooleanValue MOBS_ADD_CLOTHES;
    public static ForgeConfigSpec.DoubleValue MOBS_CYBER_ZOMBIE_DROP_RARITY;
    public static ForgeConfigSpec.DoubleValue MOBS_CLOTH_DROP_RARITY;

    public static ForgeConfigSpec.BooleanValue ENABLE_FLOAT;
    public static ForgeConfigSpec.DoubleValue HUDLENS_FLOAT;
    public static ForgeConfigSpec.DoubleValue HUDJACK_FLOAT;
    public static ForgeConfigSpec.IntValue HUDR;
    public static ForgeConfigSpec.IntValue HUDG;
    public static ForgeConfigSpec.IntValue HUDB;

    public static ForgeConfigSpec.BooleanValue SURGERY_CRAFTING;
    public static ForgeConfigSpec.BooleanValue ENABLE_KATANA;
    public static ForgeConfigSpec.BooleanValue ENABLE_CLOTHES;
    public static ForgeConfigSpec.BooleanValue ENABLE_CUSTOM_PLAYER_MODEL;
    public static ForgeConfigSpec.ConfigValue<String> FIST_MINING_TOOL_NAME;

    public static ForgeConfigSpec.BooleanValue DEFAULT_DROP;
    public static ForgeConfigSpec.BooleanValue DEFAULT_KEEP;
    public static ForgeConfigSpec.DoubleValue DROP_CHANCE;

    // for later (im not sure i even want to implement the integration (or if they have a 1.16.5 version))
    public static ForgeConfigSpec.BooleanValue INT_ENDER_IO;
    public static ForgeConfigSpec.BooleanValue INT_TOUGH_AS_NAILS;
    public static ForgeConfigSpec.BooleanValue INT_BOTANIA;
    public static ForgeConfigSpec.BooleanValue INT_MATTER_OVERDRIVE;

    static {
        BUILDER.push("Machines");
        ENGINEERING_CHANCE = BUILDER.comment("Chance of blueprint from Engineering Table")
                .defineInRange("engineeringChance", 15F, 0F, 100F);
        SCANNER_CHANCE = BUILDER.comment("Chance of blueprint from Scanner")
                .defineInRange("scannerChance", 10F, 0F, 50F);
        SCANNER_CHANCE_ADDL = BUILDER.comment("Additive chance per extra item in Scanner")
                .defineInRange("scannerChanceAddl", 10F, 0F, 100F);
        SCANNER_TIME = BUILDER.comment("Ticks per Scanner operation (24000 = 1 Minecraft day)")
                .defineInRange("scannerTime", 24000, 0, Integer.MAX_VALUE);
        TESLA_PER_POWER = BUILDER.comment("RF/Tesla per internal power unit")
                .defineInRange("teslaPerPower", 1, 0, Integer.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("Essence");
        ESSENCE = BUILDER.comment("Maximum Essence")
                .defineInRange("essence", 100, 0, Integer.MAX_VALUE);
        CRITICAL_ESSENCE = BUILDER.comment("Critical Essence value, where rejection begins")
                .defineInRange("criticalEssence", 25, 0, Integer.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("Mobs");
        MOBS_ENABLE_CYBER_ZOMBIES = BUILDER.comment("Enable CyberZombies")
                .define("enableCyberZombies", true);
        MOBS_CYBER_ZOMBIE_WEIGHT = BUILDER.comment("CyberZombie spawn weight")
                .defineInRange("cyberZombieWeight", 15, 0, Integer.MAX_VALUE);
        MOBS_CYBER_ZOMBIE_MIN_PACK = BUILDER.comment("CyberZombie min pack size")
                .defineInRange("cyberZombieMinPack", 1, 0, Integer.MAX_VALUE);
        MOBS_CYBER_ZOMBIE_MAX_PACK = BUILDER.comment("CyberZombie max pack size")
                .defineInRange("cyberZombieMaxPack", 1, 0, Integer.MAX_VALUE);
        MOBS_IS_DIMENSION_BLACKLIST = BUILDER.comment("Dimension IDs are a blacklist?")
                .define("dimensionBlacklist", true);
        MOBS_APPLY_DIMENSION_TO_SPAWNING = BUILDER.comment("Apply dimension list to natural mob spawning?")
                .define("applyDimensionToSpawning", true);
        MOBS_APPLY_DIMENSION_TO_BEACON = BUILDER.comment("Apply dimension list to beacon/radio/cranial?")
                .define("applyDimensionToBeacon", true);
        MOBS_ADD_CLOTHES = BUILDER.comment("Add Cyberware clothing to mobs")
                .define("addClothes", true);
        MOBS_CYBER_ZOMBIE_DROP_RARITY = BUILDER.comment("Chance a CyberZombie drops Cyberware")
                .defineInRange("cyberZombieDropChance", 50F, 0F, 100F);
        MOBS_CLOTH_DROP_RARITY = BUILDER.comment("Chance Cyberware clothing drops")
                .defineInRange("clothDropChance", 50F, 0F, 100F);
        BUILDER.pop();

        // --- HUD ---
        BUILDER.push("HUD");
        ENABLE_FLOAT = BUILDER.comment("Enable HUD float")
                .define("enableFloat", false);
        HUDLENS_FLOAT = BUILDER.comment("HUD lens float amount")
                .defineInRange("hudLensFloat", 0.1F, 0F, 100F);
        HUDJACK_FLOAT = BUILDER.comment("HUD jack float amount")
                .defineInRange("hudJackFloat", 0.05F, 0F, 100F);
        HUDR = BUILDER.comment("HUD Red color")
                .defineInRange("hudR", 76, 0, 255);
        HUDG = BUILDER.comment("HUD Green color")
                .defineInRange("hudG", 255, 0, 255);
        HUDB = BUILDER.comment("HUD Blue color")
                .defineInRange("hudB", 0, 0, 255);
        BUILDER.pop();

        // --- OTHER ---
        BUILDER.push("Other");
        SURGERY_CRAFTING = BUILDER.comment("Enable crafting recipe for Robosurgeon")
                .define("surgeryCrafting", false);
        ENABLE_KATANA = BUILDER.comment("Enable Katana")
                .define("enableKatana", true);
        ENABLE_CLOTHES = BUILDER.comment("Enable Trench Coat, Mirror Shades, Biker Jacket")
                .define("enableClothes", true);
        ENABLE_CUSTOM_PLAYER_MODEL = BUILDER.comment("Enable changes to player model")
                .define("enableCustomPlayerModel", true);
        FIST_MINING_TOOL_NAME = BUILDER.comment("Mining tool equivalent to reinforced fist")
                .define("fistMiningToolName", "minecraft:iron_pickaxe");
        BUILDER.pop();

        // --- GAMERULES ---
        BUILDER.push("Gamerules");
        DEFAULT_DROP = BUILDER.comment("Default for gamerule cyberware_dropCyberware")
                .define("defaultDrop", false);
        DEFAULT_KEEP = BUILDER.comment("Default for gamerule cyberware_keepCyberware")
                .define("defaultKeep", false);
        DROP_CHANCE = BUILDER.comment("Chance of successful drop if dropCyberware enabled")
                .defineInRange("dropChance", 100F, 0F, 100F);
        BUILDER.pop();
    }

    public static final ForgeConfigSpec COMMON_CONFIG = BUILDER.build();
}
