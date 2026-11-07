package com.sprint.deokhugam.review.application.provided.command;

import com.sprint.deokhugam.review.application.required.ReviewRepositoryPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReviewCommandService implements ReviewCommand {
  private final ReviewRepositoryPort repositoryPort;

  @Override
  @Transactional
  public void increaseReviewCommentCount(UUID reviewId) {
    repositoryPort.increaseCommentCount(reviewId);
  }

  @Override
  @Transactional
  public void decreaseReviewCommentCount(UUID reviewId) {
    repositoryPort.decreaseCommentCount(reviewId);
  }
}
