package com.notifan.notifan.notification;

import com.notifan.notifan.deduplication.EventDeduplicator;
import com.notifan.notifan.event.UserFollowedEvent;
import com.notifan.notifan.ratelimit.SlidingWindowRateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Persists a pending {@link Notification} for the followed user. Structurally identical to
 * {@link PostLikedEventHandler} — same single-recipient shape, same handling pattern.
 * Also checks {@link EventDeduplicator} and {@link SlidingWindowRateLimiter} before persisting.
 */
@Service
@RequiredArgsConstructor
public class UserFollowedEventHandler {

    private final NotificationRepository notificationRepository;
    private final SlidingWindowRateLimiter rateLimiter;
    private final EventDeduplicator eventDeduplicator;

    public void handle(UserFollowedEvent event) {
        if(eventDeduplicator.isDuplicated(event.eventId())) return;

        var notification = new Notification(event.recipientId(), EventType.USER_FOLLOWED);
        if(rateLimiter.isRateLimited(event.recipientId())) {
            notification.setStatus(NotificationStatus.RATE_LIMITED);
        }
        notificationRepository.save(notification);
    }
}
