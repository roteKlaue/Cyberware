package flaxbeard.cyberware.api.hud;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.client.MainWindow;

@AllArgsConstructor
@Data
public class ScaledResolution {
    public final int width;
    public final int height;

    public float getAspectRatio() {
        return (float) width / height;
    }

    public static ScaledResolution of(MainWindow window) {
        return new ScaledResolution(window.getGuiScaledWidth(), window.getGuiScaledHeight());
    }
}
