package com.sprint.deokhugam.review.adapter.persistence;

import com.sprint.deokhugam.review.application.required.ReviewLikePersistencePort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewLikeJpaRepositoryAdapter implements ReviewLikePersistencePort {
  private final ReviewLikeJpaRepository reviewLikeJpaRepository;

  @Override
  public boolean isLikedBy(UUID reviewId, UUID userId) {
    return reviewLikeJpaRepository.existsByReview_IdAndUser_IdAndLikedTrue(reviewId, userId);
  }
}
