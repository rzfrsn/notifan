package com.notifan.notifan.notification;

import com.notifan.notifan.deduplication.EventDeduplicator;
import com.notifan.notifan.event.PostLikedEvent;
import com.notifan.notifan.ratelimit.SlidingWindowRateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Persists a pending {@link Notification} for the post's author.
 * Also checks {@link EventDeduplicator} and {@link SlidingWindowRateLimiter} before persisting.
 */
@Service
@RequiredArgsConstructor
public class PostLikedEventHandler {

    private final NotificationRepository notificationRepository;
    private final SlidingWindowRateLimiter rateLimiter;
    private final EventDeduplicator eventDeduplicator;

    public void handle(PostLikedEvent event) {
        if(eventDeduplicator.isDuplicated(event.eventId())) return;

        Notification notification = new Notification(event.recipientId(), EventType.POST_LIKED);
        if(rateLimiter.isRateLimited(event.recipientId())) {
            notification.setStatus(NotificationStatus.RATE_LIMITED);
        }
        notificationRepository.save(notification);
    }
}
