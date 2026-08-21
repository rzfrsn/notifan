package com.notifan.notifan.ratelimit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the sliding window's actual admit/reject/reset behavior against real Redis — not
 * mocked, same philosophy as every other integration test in this project. Overrides the
 * real 10/1m config down to 3/5s so the window-reset assertion doesn't require a slow test.
 */
@SpringBootTest
@TestPropertySource(properties = {
        "application.rate-limit.max-requests=3",
        "application.rate-limit.window=5s"
})
public class SlidingWindowRateLimiterTest {

    @Autowired
    private SlidingWindowRateLimiter rateLimiter;

    /**
     * First 3 calls should be allowed (under the limit), the 4th should be rate-limited,
     * and after the 5-second window fully elapses, a 5th call should be allowed again.
     */
    @Test
    void allowsUpToLimitThenBlocksThenResetsAfterWindow() throws InterruptedException {
        UUID recipientId = UUID.randomUUID();

        for (int i = 0; i < 3; i++) {
            assertThat(rateLimiter.isRateLimited(recipientId)).isFalse();
        }

        assertThat(rateLimiter.isRateLimited(recipientId)).isTrue();

        // Wait past the 5s window, plus a small buffer to avoid a flaky boundary race.
        Thread.sleep(5_500);

        assertThat(rateLimiter.isRateLimited(recipientId)).isFalse();
    }
}
