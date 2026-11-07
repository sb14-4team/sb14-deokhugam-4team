package com.sprint.deokhugam.review.application.provided.query;

import com.sprint.deokhugam.review.application.dto.res.BookReviewStatsResponse;
import com.sprint.deokhugam.review.application.required.ReviewQueryPort;
import com.sprint.deokhugam.review.application.required.ReviewRepositoryPort;
import com.sprint.deokhugam.review.domain.Review;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ReviewQueryService implements ReviewFinder {
  private final ReviewRepositoryPort reviewRepositoryPort;
  private final ReviewQueryPort reviewQueryPort;

  @Override
  @Transactional
  public Review getByIdOrThrow(UUID reviewId) {
    return reviewRepositoryPort.getByIdOrThrow(reviewId);
  }

  @Override
  @Transactional
  public Review getByIdForUpdateOrThrow(UUID reviewId) {
    return reviewRepositoryPort.getByIdForUpdateOrThrow(reviewId);
  }

  @Override
  @Transactional
  public void requireActiveById(UUID reviewId) {
    reviewRepositoryPort.requireActiveById(reviewId);
  }

  @Override
  @Transactional
  public List<BookReviewStatsResponse> aggregateByBook(ReviewAggregationRange range) {
    return reviewQueryPort.aggregateByBook(range);
  }
}
