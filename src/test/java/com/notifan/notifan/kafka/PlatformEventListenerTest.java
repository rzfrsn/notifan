package com.notifan.notifan.kafka;

import com.notifan.notifan.notification.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.time.Duration;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Integration tests for the platform event Kafka consumer, run against a real embedded broker.
 */
@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = "platform-events")
public class PlatformEventListenerTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

    /**
     * Sends raw JSON, not a hand-built Java object — proves the real deserialization wiring
     *  (Jackson polymorphic resolution, ErrorHandlingDeserializer) works end to end.
     */
    @Test
    void consumesRealJsonAndPersistsNotification() {
        UUID recipientId = UUID.randomUUID();

        // Deliberately raw JSON, matching what an external producer would actually send
        // exercises the real "eventType" discriminator field, not a shortcut around it.
        String json = """
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
        kafkaTemplate.send("platform-events", recipientId.toString(), json);

        // Kafka consumption is async - poll instead of asserting immediately.
        await().atMost(Duration.ofSeconds(10)).untilAsserted(() ->
                assertThat(notificationRepository.findAll())
                    .anyMatch(n -> n.getRecipientId().equals(recipientId)));
    }
}
