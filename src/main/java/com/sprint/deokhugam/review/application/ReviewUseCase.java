package com.sprint.deokhugam.review.application;

import com.sprint.deokhugam.common.pagination.CursorPageResponse;
import com.sprint.deokhugam.review.application.dto.req.CreateReviewCommand;
import com.sprint.deokhugam.review.application.dto.req.ReviewSearchCondition;
import com.sprint.deokhugam.review.application.dto.res.ReviewResponse;
import java.util.UUID;

public interface ReviewUseCase {
  ReviewResponse createReview(CreateReviewCommand command);

  ReviewResponse getReviewDetail(UUID reviewId, UUID requestUserId);

  ReviewResponse updateReview(UUID userId, UUID reviewId, String newContent, int newRating);

  CursorPageResponse<ReviewResponse> searchReviewsByPagination(
      ReviewSearchCondition condition, UUID requestUserId);
}
