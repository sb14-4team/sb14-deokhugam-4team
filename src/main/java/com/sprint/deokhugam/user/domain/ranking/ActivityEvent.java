package com.sprint.deokhugam.user.domain.ranking;

import java.time.Instant;
import java.util.UUID;

public sealed interface ActivityEvent permits ActivityEvent.LikeAdded, ActivityEvent.CommentAdded {

  UUID eventId();

  UUID actorId();

  Instant occurredAt();

  static ActivityEvent LikeAdded(
      UUID eventId,
      UUID reviewId,
      UUID authorId,
      UUID actorId,
      Instant reviewCreatedAt,
      Instant occurredAt) {
    return new ActivityEvent.LikeAdded(
        eventId, reviewId, authorId, actorId, reviewCreatedAt, occurredAt);
  }

  static ActivityEvent CommentAdded(
      UUID eventId,
      UUID reviewId,
      UUID authorId,
      UUID actorId,
      Instant reviewCreatedAt,
      Instant occurredAt) {
    return new ActivityEvent.CommentAdded(
        eventId, reviewId, authorId, actorId, reviewCreatedAt, occurredAt);
  }

  record LikeAdded(
      UUID eventId,
      UUID reviewId,
      UUID authorId,
      UUID actorId,
      Instant reviewCreatedAt,
      Instant occurredAt)
      implements ActivityEvent {}

  record CommentAdded(
      UUID eventId,
      UUID reviewId,
      UUID authorId,
      UUID actorId,
      Instant reviewCreatedAt,
      Instant occurredAt)
      implements ActivityEvent {}
}
