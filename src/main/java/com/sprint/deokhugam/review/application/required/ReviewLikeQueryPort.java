package com.sprint.deokhugam.review.application.required;

import java.util.List;
import java.util.UUID;

public interface ReviewLikeQueryPort {
  boolean isLikedBy(UUID reviewId, UUID userId);

  List<UUID> findReviewLikeIdsByRequestId(List<UUID> reviewIds, UUID requestUserId);
}
