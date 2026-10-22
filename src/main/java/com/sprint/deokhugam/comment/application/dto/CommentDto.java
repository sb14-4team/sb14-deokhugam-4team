package com.sprint.deokhugam.comment.application.dto;

import java.time.Instant;
import java.util.UUID;

public record CommentDto(
    UUID id,
    UUID reviewId,
    UUID userId,
    String userNickname,
    String content,
    Instant createAt,
    Instant updateAt) {

  public static CommentDto to(
      UUID id,
      UUID reviewId,
      UUID userId,
      String userNickname,
      String content,
      Instant createAt,
      Instant updateAt) {
    return new CommentDto(id, reviewId, userId, userNickname, content, createAt, updateAt);
  }
}
