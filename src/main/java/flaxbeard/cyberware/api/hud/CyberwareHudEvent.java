package flaxbeard.cyberware.api.hud;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.MainWindow;
import net.minecraftforge.eventbus.api.Event;

import java.util.ArrayList;
import java.util.List;

public class CyberwareHudEvent extends Event {
    @Getter
    private final List<IHudElement> elements = new ArrayList<>();
    @Setter
    @Getter
    private boolean hudjackAvailable;
    private final MainWindow scaledResolution;

    public CyberwareHudEvent(MainWindow scaledResolution, boolean hudjackAvailable) {
        super();
        this.scaledResolution = scaledResolution;
        this.hudjackAvailable = hudjackAvailable;
    }

    public MainWindow getResolution() {
        return scaledResolution;
    }

    public void addElement(IHudElement element) {
        elements.add(element);
    }
}
