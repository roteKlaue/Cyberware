package flaxbeard.cyberware.common.gamerules;

import flaxbeard.cyberware.common.CyberwareConfig;
import net.minecraft.world.GameRules;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Method;


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
        catch (Exception e) { e.printStackTrace(); }
        return null;
    }
}
