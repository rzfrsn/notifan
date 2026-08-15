package com.notifan.notifan.event;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Fired when {@code actorId} comments on a post. Fans out to multiple recipients: the post
 * author plus every previous commenter on that post.
 * <p>
 * {@code recipientIds} arrives pre-resolved from the upstream producer — Notifan does not look up
 * who previously commented, it only receives the already-computed fan-out target list. One event
 * here produces one {@code Notification} row per entry in this list.
 *
 * @param eventId      producer-assigned unique identifier for this occurrence
 * @param actorId      the user who posted the new comment
 * @param postId       the post that was commented on
 * @param commentId    the comment that was created
 * @param recipientIds every user to notify, already resolved and deduplicated upstream
 * @param timestamp    when the comment actually happened in the source domain
 */
public record CommentAddedEvent(
        UUID eventId,
        UUID actorId,
        UUID postId,
        UUID commentId,
        List<UUID> recipientIds,
        Instant timestamp
) implements PlatformEvent {}
