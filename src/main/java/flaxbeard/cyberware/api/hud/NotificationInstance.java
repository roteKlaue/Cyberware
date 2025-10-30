package flaxbeard.cyberware.api.hud;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationInstance {
    private final float time;
    private final INotification notification;
}
