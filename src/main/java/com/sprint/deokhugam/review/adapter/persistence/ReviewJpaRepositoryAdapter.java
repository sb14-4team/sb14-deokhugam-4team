package com.sprint.deokhugam.review.adapter.persistence;

import com.sprint.deokhugam.review.application.dto.req.ReviewSearchCondition;
import com.sprint.deokhugam.review.application.required.ReviewPersistencePort;
import com.sprint.deokhugam.review.domain.Review;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

// r2dbc redis
@RequiredArgsConstructor
@Repository
public class ReviewJpaRepositoryAdapter implements ReviewPersistencePort {
  private final ReviewJpaRepository reviewJpaRepository;
  private final ReviewQuerydslRepository reviewQuerydslRepository;

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

  @Override
  public List<Review> search(ReviewSearchCondition condition) {
    return reviewQuerydslRepository.search(condition);
  }

  @Override
  public long count(ReviewSearchCondition condition) {
    return reviewQuerydslRepository.count(condition);
  }
}
