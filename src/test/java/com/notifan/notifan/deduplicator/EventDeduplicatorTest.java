package com.notifan.notifan.deduplicator;

import com.notifan.notifan.deduplication.EventDeduplicator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Confirms a duplicate is detected on the 2nd call, and the cache forgets it after TTL expiry.
 */
@SpringBootTest
@TestPropertySource(properties = "application.event-deduplication.cache-ttl=500ms")
class EventDeduplicatorTest {

    @Autowired
    private EventDeduplicator eventDeduplicator;

    /**
     * Confirms a duplicate is detected on the 2nd call, and the cache forgets it after TTL expiry.
     */
    @Test
    void removesFromCacheAfterTtl() throws InterruptedException {
        UUID eventId = UUID.randomUUID();

        assertThat(eventDeduplicator.isDuplicated(eventId)).isFalse();
        assertThat(eventDeduplicator.isDuplicated(eventId)).isTrue();

        // Wait past TLL
        Thread.sleep(600);

        assertThat(eventDeduplicator.isDuplicated(eventId)).isFalse();
    }
}
