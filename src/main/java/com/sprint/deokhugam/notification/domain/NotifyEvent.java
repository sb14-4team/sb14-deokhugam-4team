package com.sprint.deokhugam.notification.domain;

import java.util.UUID;

public sealed interface NotifyEvent permits NotifyEvent.Like, NotifyEvent.Comment {

  UUID reviewId();

  UUID userId();

  String actorName();

  static NotifyEvent like(UUID reviewId, UUID userId, String actorName) {
    return new Like(reviewId, userId, actorName);
  }

  static NotifyEvent comment(UUID reviewId, UUID userId, String actorName) {
    return new Comment(reviewId, userId, actorName);
  }

  record Like(UUID reviewId, UUID userId, String actorName) implements NotifyEvent {}

  record Comment(UUID reviewId, UUID userId, String actorName) implements NotifyEvent {}
}
