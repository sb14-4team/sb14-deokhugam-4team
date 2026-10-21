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

/*
 유저   = 유저 앱 서비스 -> 유저 + 유저스테이터스 패키지의 커맨드 유스케이스 호출
 프라이빗 파이널로 유저 삭제 유스케이스
*/
@RequiredArgsConstructor
@Service
public class ReviewCommandService implements ReviewCommand {
  private final ReviewPersistencePort reviewPersistencePort;

  @Override
  public Review create(Review review) {
    Objects.requireNonNull(review, "review");
    Objects.requireNonNull(review.getUser(), "review.user"); // 외부에서 혹시 사용할때
    Objects.requireNonNull(review.getBook(), "review.book");

    if (reviewPersistencePort.existsByUserIdAndBookId(
        review.getUser().getId(), review.getBook().getId())) {
      throw new BusinessException(ReviewErrorCode.ALREADY_EXISTS);
    }

    return reviewPersistencePort.save(review);
  }

  @Override
  public void deleteReview(UUID reviewId) {
    // adapter -> port <- application
  }
}
