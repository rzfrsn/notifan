package com.notifan.notifan.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.Instant;
import java.util.UUID;

/**
 * Common contract for every event Notifan consumes from the {@code platform-events} Kafka topic.
 * <p>
 * Sealed to exactly the three permitted implementations so that any {@code switch} over the
 * concrete type is exhaustively checked by the compiler at compile time — adding a fourth event
 * type without updating the routing logic becomes a compile error, not a silent runtime gap.
 * <p>
 * Jackson resolves the concrete subtype from the {@code eventType} field already present in the
 * incoming JSON (see {@link JsonSubTypes}), so no extra wrapper field is required beyond what the
 * producer already sends.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "eventType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PostLikedEvent.class, name = "POST_LIKED"),
        @JsonSubTypes.Type(value = CommentAddedEvent.class, name = "COMMENT_ADDED"),
        @JsonSubTypes.Type(value = UserFollowedEvent.class, name = "USER_FOLLOWED")
})
public sealed interface PlatformEvent permits PostLikedEvent, CommentAddedEvent, UserFollowedEvent {
    /** Producer-assigned unique identifier for this specific occurrence of the event. Used as the
     *  Redis deduplication key, independent of any Kafka-level offset or partition. */
    UUID eventId();

    /** When the event actually occurred in the source domain (e.g. the moment the like happened),
     *  not to be confused with Kafka's own broker-assigned message timestamp. */
    Instant timestamp();
}
