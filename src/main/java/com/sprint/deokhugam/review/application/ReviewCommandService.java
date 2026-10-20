package com.sprint.deokhugam.review.application;

import com.sprint.deokhugam.review.application.dto.CreateReviewCommand;
import com.sprint.deokhugam.review.application.provided.command.ReviewCommand;
import com.sprint.deokhugam.review.application.required.ReviewPersistencePort;
import com.sprint.deokhugam.review.domain.Review;
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
  public Review create(CreateReviewCommand command) {
    return null;
  }

  @Override
  public void deleteReview(UUID reviewId) {
    // adapter -> port <- application
  }
}
