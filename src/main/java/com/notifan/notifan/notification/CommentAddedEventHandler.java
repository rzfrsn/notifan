package com.notifan.notifan.notification;

import com.notifan.notifan.event.CommentAddedEvent;
import com.notifan.notifan.ratelimit.SlidingWindowRateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Persists one pending {@link Notification} per recipient in a {@link CommentAddedEvent} — the
 * fan-out case, unlike {@link PostLikedEventHandler} and {@link UserFollowedEventHandler}, which
 * each handle exactly one recipient.
 */
@Service
@RequiredArgsConstructor
public class CommentAddedEventHandler {

    private final NotificationRepository notificationRepository;
    private final SlidingWindowRateLimiter rateLimiter;

    public void handle(CommentAddedEvent event) {
        List<Notification> notifications = event.recipientIds().stream()
                .map(recipientId -> {
                    var notification = new Notification(recipientId, EventType.COMMENT_ADDED);
                    if(rateLimiter.isRateLimited(notification.getRecipientId())) {
                        notification.setStatus(NotificationStatus.RATE_LIMITED);
                    }
                    return notification;
                })
                .toList();
        notificationRepository.saveAll(notifications);
    }
}
