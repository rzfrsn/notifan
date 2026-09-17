package com.notifan.notifan.deduplication;

import com.notifan.notifan.config.EventDeduplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Detects duplicate Kafka events by eventId, using Redis as a short-lived seen-cache.
 * Guards against at-least-once delivery causing duplicate notifications after a
 * consumer crash and replay.
 */
@Component
@RequiredArgsConstructor
public class EventDeduplicator {
    private final StringRedisTemplate stringRedisTemplate;
    private final EventDeduplicationProperties properties;

    /**
     * Checks whether this eventId was already seen, and marks it as seen if not.
     *
     * @param eventId the event's unique identifier
     * @return true if this is a duplicate, false if seen for the first time
     */
    public Boolean isDuplicated(UUID eventId) {
        String key = "event:" + eventId;
        Boolean isNew = stringRedisTemplate.opsForValue()
                .setIfAbsent(key, "1", properties.getCacheTtl());

        return isNew == null || !isNew;
    }
}
