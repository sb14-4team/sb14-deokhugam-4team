package com.sprint.deokhugam.review.application.required;

import com.sprint.deokhugam.review.domain.Review;
import java.util.UUID;

public interface ReviewPersistencePort {
  Review save(Review review);

  Review getByIdOrThrow(UUID reviewId);

  void requireActiveById(UUID reviewId);

  boolean existsByUserIdAndBookId(UUID userId, UUID bookId);
}
