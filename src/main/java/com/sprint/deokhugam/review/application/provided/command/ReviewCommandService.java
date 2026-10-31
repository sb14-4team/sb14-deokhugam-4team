package com.sprint.deokhugam.review.application.provided.command;

import com.sprint.deokhugam.review.application.required.ReviewRepositoryPort;
import com.sprint.deokhugam.review.domain.Review;
import java.util.List;
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
  private final ReviewRepositoryPort reviewRepositoryPort;

  @Override
  @Transactional
  public long deleteReviewByIds(List<UUID> reviewIds) {
    if (reviewIds.isEmpty()) return 0L;
    List<Review> reviews = reviewRepositoryPort.getByIdsIncludingDeleted(reviewIds);

    return reviewRepositoryPort.deleteAllByIds(reviewIds);
  }
}
