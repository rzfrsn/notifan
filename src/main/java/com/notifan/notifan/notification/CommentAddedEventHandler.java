package com.notifan.notifan.notification;

import com.notifan.notifan.event.CommentAddedEvent;
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

    public void handle(CommentAddedEvent event) {
        List<Notification> notifications = event.recipientIds().stream()
                .map(id -> new Notification(id, EventType.COMMENT_ADDED))
                .toList();

        notificationRepository.saveAll(notifications);
    }
}
