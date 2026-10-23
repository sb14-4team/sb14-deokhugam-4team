package com.sprint.deokhugam.review.application.dto.res;

import com.sprint.deokhugam.review.domain.ReviewLike;
import java.util.UUID;

public record ReviewLikeResponse(UUID reviewId, UUID userId, boolean liked) {
  public static ReviewLikeResponse from(ReviewLike reviewLike) {
    return new ReviewLikeResponse(
        reviewLike.getReview().getId(), reviewLike.getUser().getId(), reviewLike.isLiked());
  }
}
