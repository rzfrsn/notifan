package com.notifan.notifan.notification;

import com.notifan.notifan.event.UserFollowedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Persists a pending {@link Notification} for the followed user. Structurally identical to
 * {@link PostLikedEventHandler} — same single-recipient shape, same handling pattern.
 */
@Service
@RequiredArgsConstructor
public class UserFollowedEventHandler {

    private final NotificationRepository notificationRepository;

    public void handle(UserFollowedEvent event) {
        var notification = new Notification(event.recipientId(), EventType.USER_FOLLOWED);
        notificationRepository.save(notification);
    }
}
