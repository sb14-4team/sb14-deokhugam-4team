package com.sprint.deokhugam.review.application.provided.command;

import java.util.UUID;

public interface ReviewCommand {
  void increaseReviewCommentCount(UUID reviewId);

  void decreaseReviewCommentCount(UUID reviewId);
}
