package flaxbeard.cyberware.api.hud;

import lombok.Getter;
import lombok.Setter;
import net.minecraftforge.eventbus.api.Event;

import java.util.ArrayList;
import java.util.List;

public class CyberwareHudEvent extends Event {
    @Getter
    private final List<IHudElement> elements = new ArrayList<>();
    @Setter
    @Getter
    private boolean hudjackAvailable;
    @Getter
    private final ScaledResolution resolution;

    public CyberwareHudEvent(ScaledResolution resolution, boolean hudjackAvailable) {
        super();
        this.resolution = resolution;
        this.hudjackAvailable = hudjackAvailable;
    }

    public void addElement(IHudElement element) {
        elements.add(element);
    }
}
