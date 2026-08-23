package com.notifan.notifan.notification;

import com.notifan.notifan.event.UserFollowedEvent;
import com.notifan.notifan.ratelimit.SlidingWindowRateLimiter;
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
    private final SlidingWindowRateLimiter rateLimiter;

    public void handle(UserFollowedEvent event) {
        var notification = new Notification(event.recipientId(), EventType.USER_FOLLOWED);
        if(rateLimiter.isRateLimited(event.recipientId())) {
            notification.setStatus(NotificationStatus.RATE_LIMITED);
        }
        notificationRepository.save(notification);
    }
}
