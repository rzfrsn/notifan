package com.notifan.notifan.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Fired when {@code actorId} likes a post belonging to {@code recipientId}.
 * Simplest event type: exactly one recipient, no fan-out.
 * <p>
 * A viral post can make {@code recipientId} a hot key — a burst of likes for one recipient in a
 * short window. The Phase 3 rate limiter exists to absorb this.
 *
 * @param eventId     producer-assigned unique identifier for this occurrence
 * @param actorId     the user who performed the like
 * @param recipientId the post author — sole recipient of this notification
 * @param postId      the post that was liked
 * @param timestamp   when the like actually happened in the source domain
 */
public record PostLikedEvent(
        UUID eventId,
        UUID actorId,
        UUID recipientId,
        UUID postId,
        Instant timestamp
) implements PlatformEvent {}
