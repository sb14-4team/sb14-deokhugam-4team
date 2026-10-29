package com.sprint.deokhugam.review.application.provided.query;

import com.sprint.deokhugam.review.application.required.ReviewPersistencePort;
import com.sprint.deokhugam.review.domain.Review;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 읽기 전용 구현체
*/
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ReviewQueryService implements ReviewFinder {
  private final ReviewPersistencePort reviewPersistencePort;

  @Override
  public Review getByIdOrThrow(UUID reviewId) {
    return reviewPersistencePort.getByIdOrThrow(reviewId);
  }

  @Override
  public void requireActiveById(UUID reviewId) {
    reviewPersistencePort.requireActiveById(reviewId);
  }

  @Override
  public List<UUID> getReviewIdsByDeleteUserIds(List<UUID> userIds) {
    if (userIds.isEmpty()) return List.of();

    return reviewPersistencePort.getIdsByUserIds(userIds);
  }
}
