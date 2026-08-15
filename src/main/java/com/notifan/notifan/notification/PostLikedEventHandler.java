package com.notifan.notifan.notification;

import com.notifan.notifan.event.PostLikedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Persists a pending {@link Notification} for the post's author. No rate limiting, dedup, or
 *  delivery yet - those are Phase 3. This proves the Kafka-to-Postgres path works end to end.
 */
@Service
@RequiredArgsConstructor
public class PostLikedEventHandler {

    private final NotificationRepository notificationRepository;

    public void handle(PostLikedEvent event) {
        Notification notification = new Notification(event.recipientId(), EventType.POST_LIKED);
        notificationRepository.save(notification);
    }
}
