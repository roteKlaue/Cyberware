package flaxbeard.cyberware.api.hud;

import lombok.Getter;

public class NotificationInstance {
    @Getter
    private final float time;
    @Getter
    private final INotification notification;

    public NotificationInstance(float time, INotification notification) {
        this.time = time;
        this.notification = notification;
    }
}
