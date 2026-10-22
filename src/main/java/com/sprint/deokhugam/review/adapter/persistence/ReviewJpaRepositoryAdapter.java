package com.sprint.deokhugam.review.adapter.persistence;

import com.sprint.deokhugam.review.application.required.ReviewPersistencePort;
import com.sprint.deokhugam.review.domain.Review;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

// r2dbc redis
@RequiredArgsConstructor
@Repository
public class ReviewJpaRepositoryAdapter implements ReviewPersistencePort {
  private final ReviewJpaRepository reviewJpaRepository;
  private final ReviewQuerydslRepository reviewQuerydslRepository; // 구현체주입

  @Override
  public Review save(Review review) {
    return reviewJpaRepository.save(review);
  }

  @Override
  public Review getByIdOrThrow(UUID reviewId) {
    return reviewJpaRepository.getByIdOrThrow(reviewId);
  }

  @Override
  public void requireActiveById(UUID reviewId) {
    reviewJpaRepository.requireActiveById(reviewId);
  }

  @Override
  public boolean existsByUserIdAndBookId(UUID userId, UUID bookId) {
    return reviewJpaRepository.existsActiveReview(userId, bookId);
  }
}
