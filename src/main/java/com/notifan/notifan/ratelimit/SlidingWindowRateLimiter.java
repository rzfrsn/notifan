package com.notifan.notifan.ratelimit;


import com.notifan.notifan.config.RateLimitProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SlidingWindowRateLimiter {

    private final StringRedisTemplate redisTemplate;
    private final RedisScript<Long> rateLimitScript;
    private final RateLimitProperties properties;

    /**
     * Checks whether a new notification for this recipient is allowed under the configured
     * sliding window, and atomically records it if so.
     *
     * @param recipientId the recipient being rate-limited
     * @return true if allowed, false if this recipient has hit the limit for the current window
     */
    public boolean isAllowed(UUID recipientId) {
        String key = "rate-limit:" + recipientId;
        long nowMillis = Instant.now().toEpochMilli();
        long windowMillis = properties.getWindow().toMillis();
        long ttlSeconds = properties.getWindow().toSeconds() + 1;
        String member = UUID.randomUUID().toString();

        Long result = redisTemplate.execute(
                rateLimitScript,
                List.of(key),
                String.valueOf(nowMillis),
                String.valueOf(windowMillis),
                String.valueOf(properties.getMaxRequests()),
                member,
                String.valueOf(ttlSeconds)
        );

        return result == 1L;
    }

    /**
     * Convenience negation of {@link #isAllowed(UUID)}, so call sites read naturally without
     * a double negative ("if not allowed" vs "if rate limited").
     */
    public boolean isRateLimited(UUID recipientId) {
        return !isAllowed(recipientId);
    }
}
