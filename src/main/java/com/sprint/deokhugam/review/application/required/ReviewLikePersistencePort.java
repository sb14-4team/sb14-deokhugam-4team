package com.sprint.deokhugam.review.application.required;

import com.sprint.deokhugam.review.domain.ReviewLike;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewLikePersistencePort {
  ReviewLike save(ReviewLike reviewLike);

  boolean isLikedBy(UUID reviewId, UUID userId);

  List<UUID> findReviewLikeIdsByRequestId(List<UUID> reviewIds, UUID requestUserId);

  Optional<ReviewLike> getByUserIdAndReviewId(UUID userId, UUID reviewId);

  void deleteAllByReviewId(UUID reviewId);

  long deleteAllByUserIds(List<UUID> userIds);

  void deleteAllByReviewIds(List<UUID> reviewIds);
}
