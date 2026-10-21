package com.sprint.deokhugam.review.application.required;

import java.util.UUID;

public interface ReviewLikePersistencePort {
  boolean isLikedBy(UUID reviewId, UUID userId);
}
