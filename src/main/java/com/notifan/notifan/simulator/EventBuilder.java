package com.notifan.notifan.simulator;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.notifan.notifan.event.CommentAddedEvent;
import com.notifan.notifan.event.PostLikedEvent;
import com.notifan.notifan.event.UserFollowedEvent;
import com.notifan.notifan.notification.EventType;

public class EventBuilder {

  private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());

  public static String generate(EventType type, UUID actorId, UUID recipientId) {
    try {
      return switch (type) {
        case POST_LIKED -> MAPPER.writeValueAsString(createPostLikedEvent(actorId, recipientId));
        case USER_FOLLOWED -> MAPPER.writeValueAsString(createUserFollowedEvent(actorId, recipientId));
        case COMMENT_ADDED -> MAPPER.writeValueAsString(createCommentAddedEvent(actorId));
      };
    } catch (Exception e) {
      throw new IllegalStateException("Failed to build simulated event JSON", e);
    }
  }

  private static PostLikedEvent createPostLikedEvent(UUID actorId, UUID recipientId) {
    return new PostLikedEvent(UUID.randomUUID(), actorId, recipientId, UUID.randomUUID(), Instant.now());
  }

  private static UserFollowedEvent createUserFollowedEvent(UUID actorId, UUID recipientId) {
    return new UserFollowedEvent(UUID.randomUUID(), actorId, recipientId, Instant.now());
  }

  /** Fan-out demo: recipientId is ignored here — targets every TestUser except the actor. */
  private static CommentAddedEvent createCommentAddedEvent(UUID actorId) {
    List<UUID> recipientIds = TestUsers.TEST_USERS.stream()
        .map(TestUser::id)
        .filter(id -> !id.equals(actorId))
        .toList();
    return new CommentAddedEvent(UUID.randomUUID(), actorId, UUID.randomUUID(), UUID.randomUUID(), recipientIds,
        Instant.now());
  }
}
