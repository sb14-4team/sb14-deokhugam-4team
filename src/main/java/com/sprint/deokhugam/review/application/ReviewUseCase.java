package com.sprint.deokhugam.review.application;

import com.sprint.deokhugam.review.application.dto.req.CreateReviewCommand;
import com.sprint.deokhugam.review.application.dto.res.ReviewResponse;
import java.util.UUID;

public interface ReviewUseCase {
  ReviewResponse createReview(CreateReviewCommand command);

  ReviewResponse getReviewDetail(UUID reviewId, UUID userId);

  ReviewResponse updateReview(UUID userId, UUID reviewId, String newContent, int newRating);
}
