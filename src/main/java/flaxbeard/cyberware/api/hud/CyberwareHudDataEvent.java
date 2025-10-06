package flaxbeard.cyberware.api.hud;

import lombok.Getter;
import net.minecraftforge.eventbus.api.Event;

import java.util.ArrayList;
import java.util.List;

public class CyberwareHudDataEvent extends Event {
    @Getter
    private final List<IHudElement> elements = new ArrayList<>();

    public void addElement(IHudElement element) {
        elements.add(element);
    }
}
