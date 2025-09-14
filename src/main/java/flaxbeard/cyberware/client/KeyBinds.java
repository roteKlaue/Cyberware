package flaxbeard.cyberware.client;

import net.minecraft.client.util.InputMappings;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class KeyBinds {
    public static KeyBinding menu;

    public static void init() {
        menu = new KeyBinding(
                "overclockedorgans.keybinds.menu",
                InputMappings.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "overclockedorgans.keybinds.category"
        );

        ClientRegistry.registerKeyBinding(menu);
    }
}
