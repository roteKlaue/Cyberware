package flaxbeard.cyberware.api;

import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.EntityEvent;

import javax.annotation.Nonnull;

public class CyberwareUpdateEvent extends EntityEvent {
    private final ICyberwareUserData cyberwareUserData;

    public CyberwareUpdateEvent(@Nonnull LivingEntity entity, @Nonnull ICyberwareUserData cyberwareUserData) {
        super(entity);
        this.cyberwareUserData = cyberwareUserData;
    }

    @Nonnull
    public ICyberwareUserData getCyberwareUserData()
    {
        return cyberwareUserData;
    }
}
