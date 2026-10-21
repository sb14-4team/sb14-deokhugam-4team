package com.sprint.deokhugam.review.application.provided.command;

import com.sprint.deokhugam.review.domain.Review;
import java.util.UUID;

public interface ReviewCommand {
  Review create(Review review);

  void deleteReview(UUID reviewId); // 물리 삭제
}
