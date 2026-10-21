package com.sprint.deokhugam.review.adapter.persistence;

import com.sprint.deokhugam.review.application.required.ReviewPersistencePort;
import com.sprint.deokhugam.review.domain.Review;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ReviewRepositoryAdapter implements ReviewPersistencePort {
  private final ReviewJpaRepository reviewRepository;

  // r2dbc redis
  @Override
  public Review save(Review review) {
    return reviewRepository.save(review);
  }

  @Override
  public Review getByIdOrThrow(UUID reviewId) {
    return reviewRepository.getByIdOrThrow(reviewId);
  }

  @Override
  public boolean existsByUserIdAndBookId(UUID userId, UUID bookId) {
    return reviewRepository.existsActiveReview(userId, bookId);
  }

  @Override
  public boolean existsActiveById(UUID reviewId) {
    return reviewRepository.existsByIdAndDeletedAtIsNull(reviewId);
  }
}
