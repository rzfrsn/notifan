package com.notifan.notifan.kafka;

import com.notifan.notifan.event.CommentAddedEvent;
import com.notifan.notifan.event.PlatformEvent;
import com.notifan.notifan.event.PostLikedEvent;
import com.notifan.notifan.event.UserFollowedEvent;
import com.notifan.notifan.notification.CommentAddedEventHandler;
import com.notifan.notifan.notification.PostLikedEventHandler;
import com.notifan.notifan.notification.UserFollowedEventHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumes {@code platform-events} and routes by concrete event type. The switch is exhaustive
 * over {@link PlatformEvent}'s sealed permits — a new event type added without a case here fails
 * to compile, rather than silently falling through at runtime.
 */
@Component
@RequiredArgsConstructor
public class PlatformEventListener {

    private final PostLikedEventHandler postLikedEventHandler;
    private final UserFollowedEventHandler userFollowedEventHandler;
    private final CommentAddedEventHandler commentAddedEventHandler;

    @KafkaListener(topics = "${application.platform-events-topic}")
    public void onEvent(PlatformEvent event) {
        switch (event) {
            case PostLikedEvent e -> postLikedEventHandler.handle(e);
            case CommentAddedEvent e -> commentAddedEventHandler.handle(e);
            case UserFollowedEvent e -> userFollowedEventHandler.handle(e);
        }
    }
}
