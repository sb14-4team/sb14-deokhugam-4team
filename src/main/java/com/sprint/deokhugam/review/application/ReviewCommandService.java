package com.sprint.deokhugam.review.application;

import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.domain.review.exception.ReviewErrorCode;
import com.sprint.deokhugam.review.application.provided.command.ReviewCommand;
import com.sprint.deokhugam.review.application.required.ReviewPersistencePort;
import com.sprint.deokhugam.review.domain.Review;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 유저   = 유저 앱 서비스 -> 유저 + 유저스테이터스 패키지의 커맨드 유스케이스 호출
 프라이빗 파이널로 유저 삭제 유스케이스
*/
@RequiredArgsConstructor
@Service
public class ReviewCommandService implements ReviewCommand {
  private final ReviewPersistencePort reviewPersistencePort;

  @Override
  @Transactional
  public Review createReview(Review review) {
    Objects.requireNonNull(review, "review");
    if (reviewPersistencePort.existsByUserIdAndBookId(
        review.getUser().getId(), review.getBook().getId())) {
      throw new BusinessException(ReviewErrorCode.ALREADY_EXISTS);
    }

    return reviewPersistencePort.save(review);
  }

  @Override
  @Transactional
  public Review updateReview(UUID reviewId, UUID userId, String newContent, int newRating) {
    Review review = reviewPersistencePort.getByIdOrThrow(reviewId);

    if (!review.verifyReviewUpdate(userId)) {
      throw new BusinessException(ReviewErrorCode.FORBIDDEN);
    }

    review.updateContentAndRating(newContent, newRating);
    return review;
  }

  @Override
  public void deleteReview(UUID reviewId) {
    // adapter -> port <- application
  }
}
