package com.sprint.deokhugam.review.application.provided.command;

import com.sprint.deokhugam.review.domain.Review;
import java.util.UUID;

public interface ReviewCommand {
  Review createReview(Review review);

  Review updateReview(UUID reviewId, UUID userId, String newContent, int newRating);

  void deleteReview(UUID reviewId); // 물리 삭제
}
