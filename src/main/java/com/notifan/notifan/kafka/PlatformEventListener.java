package com.notifan.notifan.kafka;

import com.notifan.notifan.event.CommentAddedEvent;
import com.notifan.notifan.event.PlatformEvent;
import com.notifan.notifan.event.PostLikedEvent;
import com.notifan.notifan.event.UserFollowedEvent;
import com.notifan.notifan.notification.PostLikedEventHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Consumes {@code platform-events} and routes by concrete event type. The switch is exhaustive
 * over {@link PlatformEvent}'s sealed permits — a new event type added without a case here fails
 * to compile, rather than silently falling through at runtime.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PlatformEventListener {

    private final PostLikedEventHandler postLikedEventHandler;

    @KafkaListener(topics = "platform-events")
    public void onEvent(PlatformEvent event) {
        switch (event) {
            case PostLikedEvent e -> postLikedEventHandler.handle(e);
            case CommentAddedEvent e -> log.warn("COMMENT_ADDED handling not yet implemented, eventId={}", e.eventId());
            case UserFollowedEvent e -> log.warn("USER_FOLLOWED handling not yet implemented, eventId={}", e.eventId());
        }
    }
}
