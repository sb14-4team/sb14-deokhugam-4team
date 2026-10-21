package com.sprint.deokhugam.review.application.dto;

import com.sprint.deokhugam.review.domain.Review;
import java.time.Instant;
import java.util.UUID;

// 어플리케이션 - 서비스에서 사용하는 인바운드 아웃바운드 포트를 커맨드 쿼리로
public record ReviewResponse(
    UUID id,
    UUID bookId,
    String bookTitle,
    String bookThumbnailUrl,
    UUID userId,
    String userNickname,
    String content,
    int rating,
    int likeCount,
    int commentCount,
    boolean likedByMe,
    Instant createdAt,
    Instant updatedAt) {

  public static ReviewResponse from(
      Review entity, boolean likedByMe, Instant createdAt, Instant updatedAt) {
    return new ReviewResponse(
        entity.getId(),
        entity.getBook().getId(),

        // entity.getBook().getTitle(),
        null,

        // entity.getBook().getThumbnailUrl(),
        null,
        entity.getUser().getId(),

        // entity.getUser().getNickname(),
        null,
        entity.getContent(),
        entity.getRating(),
        entity.getLikeCount(),
        entity.getCommentCount(),
        likedByMe,
        createdAt,
        updatedAt);
  }
}
