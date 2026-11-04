package com.sprint.deokhugam.dashboard.review.adapter.scheduler;

import com.sprint.deokhugam.dashboard.review.application.provided.PopularReviewUseCase;
import com.sprint.deokhugam.dashboard.review.domain.PopularReviewPeriod;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PopularReviewScoreScheduler {

  private final PopularReviewUseCase popularReviewUseCase;

  // 매일 0시 0분 0초에 실행
  @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
  public void calculatePopularReviews() {
    popularReviewUseCase.aggregatePopularReviews(LocalDate.now(PopularReviewPeriod.ZONE));
  }
}
