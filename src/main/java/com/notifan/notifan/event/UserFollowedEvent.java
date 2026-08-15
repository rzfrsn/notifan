package com.notifan.notifan.event;

import java.time.Instant;
import java.util.UUID;

/**
 * Fired when {@code actorId} follows {@code recipientId}. Structurally near-identical to
 * {@link PostLikedEvent} but modeled separately - see project specs, "Why separate event
 * types," for the reasoning. Subject to the same hot-key rate-limiting concern as
 * {@link PostLikedEvent}, not something unique to follows.
 *
 * @param eventId     producer-assigned unique identifier for this occurrence
 * @param actorId     the user who initiated the follow
 * @param recipientId the user being followed - sole recipient of this notification
 * @param timestamp   when the follow actually happened in the source domain
 */
public record UserFollowedEvent(
        UUID eventId,
        UUID actorId,
        UUID recipientId,
        Instant timestamp
) implements PlatformEvent {}
