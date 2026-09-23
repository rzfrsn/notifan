package com.notifan.notifan.notification;

import org.springframework.stereotype.Service;

import com.notifan.notifan.deduplication.EventDeduplicator;
import com.notifan.notifan.delivery.NotificationDeliveryService;
import com.notifan.notifan.event.UserFollowedEvent;
import com.notifan.notifan.metrics.NotificationMetrics;
import com.notifan.notifan.ratelimit.SlidingWindowRateLimiter;

import lombok.RequiredArgsConstructor;

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
    private final NotificationDeliveryService notificationDelivery;
    private final NotificationMetrics notificationMetrics;

    public void handle(UserFollowedEvent event) {
        if (eventDeduplicator.isDuplicated(event.eventId())) return;

        var notification = new Notification(event.recipientId(), EventType.USER_FOLLOWED);
        if (rateLimiter.isRateLimited(event.recipientId())) {
            notification.setStatus(NotificationStatus.RATE_LIMITED);
        }
        var newNotification = notificationRepository.save(notification);

        if (newNotification.isRateLimited()) {
            notificationMetrics.record(newNotification.getStatus());
        } else {
            notificationDelivery.deliverAsync(newNotification);
        }
    }
}
