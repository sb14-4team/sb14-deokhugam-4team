package com.sprint.deokhugam.review.application.required;

import com.sprint.deokhugam.review.domain.ReviewLike;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewLikeRepositoryPort {
  ReviewLike save(ReviewLike reviewLike);

  Optional<ReviewLike> getByUserIdAndReviewId(UUID userId, UUID reviewId);

  void deleteAllByReviewId(UUID reviewId);

  long deleteAllByUserIds(List<UUID> userIds);

  void deleteAllByReviewIds(List<UUID> reviewIds);

  long deleteAllByUserIdsOrReviewIds(List<UUID> userIds, List<UUID> reviewIds);
}
