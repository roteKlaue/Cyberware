package flaxbeard.cyberware.client.gui;

import net.minecraft.inventory.container.Slot;

@FunctionalInterface
public interface ISlotGetter {
    Slot getSlot(int slot);
}
