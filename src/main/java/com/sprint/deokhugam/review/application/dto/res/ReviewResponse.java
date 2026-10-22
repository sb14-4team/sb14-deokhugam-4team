package com.sprint.deokhugam.review.application.dto.res;

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
  public static ReviewResponse of(Review review, boolean likedByMe) {
    return new ReviewResponse(
        review.getId(),
        review.getBook().getId(),
        review.getBook().getTitle(),
        review.getBook().getThumbnailUrl(),
        review.getUser().getId(),
        review.getUser().getNickname(),
        review.getContent(),
        review.getRating(),
        review.getLikeCount(),
        review.getCommentCount(),
        likedByMe,
        review.getCreatedAt(),
        review.getUpdatedAt());
  }
}
