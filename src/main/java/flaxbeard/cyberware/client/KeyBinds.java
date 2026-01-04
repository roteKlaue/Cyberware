package flaxbeard.cyberware.client;

import flaxbeard.cyberware.OverclockedOrgans;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class KeyBinds {
    public static final String CATEGORY = "keybinds." + OverclockedOrgans.MOD_ID + ".category";
    public static KeyBinding menu;

    public static void init() {
        menu = new KeyBind("menu", GLFW.GLFW_KEY_R);
    }

    public static class KeyBind extends KeyBinding {
        public KeyBind(String id, int keyCode) {
            super(("keybinds." + OverclockedOrgans.MOD_ID + "." + id).toLowerCase(), keyCode, CATEGORY);
            ClientRegistry.registerKeyBinding(this);
        }
    }
}
