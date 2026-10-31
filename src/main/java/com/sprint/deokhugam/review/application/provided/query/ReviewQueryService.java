package com.sprint.deokhugam.review.application.provided.query;

import com.sprint.deokhugam.review.application.required.ReviewRepositoryPort;
import com.sprint.deokhugam.review.domain.Review;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ReviewQueryService implements ReviewFinder {
  private final ReviewRepositoryPort reviewRepositoryPort;

  @Override
  public Review getByIdOrThrow(UUID reviewId) {
    return reviewRepositoryPort.getByIdOrThrow(reviewId);
  }

  @Override
  public void requireActiveById(UUID reviewId) {
    reviewRepositoryPort.requireActiveById(reviewId);
  }
}
