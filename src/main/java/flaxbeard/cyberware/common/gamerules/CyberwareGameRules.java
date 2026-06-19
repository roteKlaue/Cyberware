package flaxbeard.cyberware.common.gamerules;

import flaxbeard.cyberware.OverclockedOrgans;
import flaxbeard.cyberware.common.CyberwareConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nonnull;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;


public class CyberwareGameRules {
    public static GameRules.RuleKey<GameRules.BooleanValue> ENABLE_KEEP_WARE;
    public static GameRules.RuleKey<GameRules.BooleanValue> ENABLE_DROP_WARE;

    public static void register() {
        ENABLE_KEEP_WARE = GameRules.register(
                "keepCyberware",
                GameRules.Category.PLAYER,
                create(CyberwareConfig.DEFAULT_KEEP.get())
        );

        ENABLE_DROP_WARE = GameRules.register(
                "dropCyberware",
                GameRules.Category.PLAYER,
                create(CyberwareConfig.DEFAULT_DROP.get())
        );
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static GameRules.RuleType<GameRules.BooleanValue> create(boolean defaultValue) {
        try {
            Method m = ObfuscationReflectionHelper.findMethod(
                    GameRules.BooleanValue.class,
                    "create",
                    boolean.class
            );
            m.setAccessible(true);
            return (GameRules.RuleType<GameRules.BooleanValue>) m.invoke(GameRules.BooleanValue.class, defaultValue);
        }
        catch (Exception e) {
            OverclockedOrgans.LOGGER.error("Failed to create GameRules.BooleanValue", e);
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public static GameRules.RuleType<GameRules.BooleanValue> create(
            boolean defaultValue,
            @Nonnull BiConsumer<MinecraftServer, GameRules.BooleanValue> callback
    ) {
        try {
            Method m = ObfuscationReflectionHelper.findMethod(
                    GameRules.BooleanValue.class,
                    "create",
                    boolean.class,
                    BiConsumer.class
            );

            return (GameRules.RuleType<GameRules.BooleanValue>)
                    m.invoke(GameRules.BooleanValue.class, defaultValue, callback);
        } catch (Exception e) {
            OverclockedOrgans.LOGGER.error("Failed to create GameRules.BooleanValue", e);
            throw new RuntimeException(e);
        }
    }

}
