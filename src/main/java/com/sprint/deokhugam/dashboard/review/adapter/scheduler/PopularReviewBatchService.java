package com.sprint.deokhugam.dashboard.review.adapter.scheduler;

import com.sprint.deokhugam.dashboard.review.application.PopularReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PopularReviewBatchService {

  private final PopularReviewService popularReviewService;

  // 메일 0시 0분 0초에 실행
  @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
  public void calculatePopularReviews() {
    popularReviewService.aggregateAndSavePopularReviews();
  }
}
