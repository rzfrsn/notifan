package com.notifan.notifan.kafka;

import com.notifan.notifan.notification.EventType;
import com.notifan.notifan.notification.NotificationRepository;
import com.notifan.notifan.config.ApplicationProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Integration tests for the platform event Kafka consumer, run against a real embedded broker.
 */
@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = "${application.platform-events-topic}")
class PlatformEventListenerTest {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

    /**
     * Sends raw JSON, not a hand-built Java object — proves the real deserialization wiring
     *  (Jackson polymorphic resolution, ErrorHandlingDeserializer) works end to end.
     */
    @Test
    void consumesPostLikedEventAndPersistsNotification() {
        UUID recipientId = UUID.randomUUID();

        String topic = applicationProperties.getPlatformEventsTopic();

        // Deliberately raw JSON, matching what an external producer would actually send
        // exercises the real "eventType" discriminator field, not a shortcut around it.
        String postLikedEvent = """
                {
                  "eventType": "POST_LIKED",
                  "eventId": "%s",
                  "actorId": "%s",
                  "recipientId": "%s",
                  "postId": "%s",
                  "timestamp": "2026-08-11T10:00:00Z"
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID(), recipientId, UUID.randomUUID());

        // Key = recipientId, so all events for one recipient stay ordered on the same partition.
        kafkaTemplate.send(topic, recipientId.toString(), postLikedEvent);

        // Kafka consumption is async - poll instead of asserting immediately.
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(notificationRepository.findAll())
                    .anyMatch(n -> n.getRecipientId().equals(recipientId)
                            && n.getEventType().equals(EventType.POST_LIKED)));
    }

    /**
     * Structurally identical to {@link #consumesPostLikedEventAndPersistsNotification()} — same
     * single-recipient shape, confirms USER_FOLLOWED routes and persists correctly too.
     */
    @Test
    void consumesUserFollowedEventAndPersistsNotification() {
        UUID recipientId = UUID.randomUUID();

        String topic = applicationProperties.getPlatformEventsTopic();

        String userFollowedEvent = """
                {
                  "eventType": "USER_FOLLOWED",
                  "eventId": "%s",
                  "actorId": "%s",
                  "recipientId": "%s",
                  "timestamp": "2026-08-11T10:00:00Z"
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID(), recipientId);

        kafkaTemplate.send(topic, recipientId.toString(), userFollowedEvent);

        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(notificationRepository.findAll())
                        .anyMatch(n -> n.getRecipientId().equals(recipientId)
                                && n.getEventType().equals(EventType.USER_FOLLOWED)));
    }
}
