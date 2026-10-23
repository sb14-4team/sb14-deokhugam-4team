package com.sprint.deokhugam.review.application.dto.req;

import java.time.Instant;
import java.util.UUID;

public record ReviewSearchCondition(
    UUID userId,
    UUID bookId,
    String keyword,
    String orderBy,
    String direction,
    String cursor,
    Instant after,
    int limit) {
  public static ReviewSearchCondition toCondition(
      UUID userId,
      UUID bookId,
      String keyword,
      String orderBy,
      String direction,
      String cursor,
      Instant after,
      int limit) {
    return new ReviewSearchCondition(
        userId, bookId, keyword, orderBy, direction, cursor, after, limit);
  }
}
