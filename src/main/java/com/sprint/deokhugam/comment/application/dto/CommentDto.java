package com.sprint.deokhugam.comment.application.dto;

import com.sprint.deokhugam.comment.domain.Comment;
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

  public static CommentDto from(Comment comment) {
    return new CommentDto(
        comment.getId(),
        comment.getReview().getId(),
        comment.getUser().getId(),
        comment.getUser().getNickname(),
        comment.getContent(),
        comment.getCreatedAt(),
        comment.getUpdatedAt());
  }
}
