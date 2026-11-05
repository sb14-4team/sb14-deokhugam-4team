package com.sprint.deokhugam.dashboard.book.adapter.scheduler;

import com.sprint.deokhugam.dashboard.book.domain.RankingPeriod;
import java.time.LocalDate;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class PopularBookRankingScheduler {
  private final JobOperator jobOperator;
  private final Job popularBookRankingJob;

  /*
   도서 레코드 수 * 4 만큼 레코드 생성 문제 인지 -> 제한 으로 문제 해결하기로 결정

   추가 문제 가능성
   1. 도서의 레코드 수가 많을 경우(스프링 컨테이너의 메모리 문제) -> 쿼리레벨 계산을 고려하던지 전체 집계를 원한다면 청크로 쪼개서 가져오고 뒤 개선안을 생각
   2. 유스케이스가 변하거나 전체 조회가 필요할 경우 -> 전체 집계 결과를 별도 테이블로 제공하고 보관 기간을 정해 레코드수를 관리한다

   이전 문제 및 장점
   - 문제 : 도서의 레코드 수가 많다면 저장할 레코드수가 O(n) 만큼 늘어난다
   - 장점 : 모든 이력을 하나의 테이블로 관리할 수 있다

   변환 문제 및 장점
   - 문제 : 20위 밖의 데이터는 저장안함.
   - 장점 : 이전 문제에서 레코드수 문제를 해결
  */
  @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
  public void calculateRanking() {
    LocalDate baseDate = LocalDate.now(ZoneId.of("Asia/Seoul"));

    for (RankingPeriod period : RankingPeriod.values()) {
      JobParameters parameters =
          new JobParametersBuilder()
              .addString("period", period.name())
              .addLocalDate("baseDate", baseDate)
              .toJobParameters();

      try {
        JobExecution execution = jobOperator.start(popularBookRankingJob, parameters);
        if (execution.getStatus() == BatchStatus.COMPLETED) {
          log.info("인기도서 집계 성공 --- period = {}, baseDate = {}", period, baseDate);
        }

        // 배치 중 실패
        if (execution.getStatus() == BatchStatus.FAILED) {
          log.error("인기도서 집계 실패 --- period = {}, baseDate = {}", period, baseDate);
        }
      } catch (Exception e) {
        // 배치 호출 중 실패
        log.error("랭킹 배치작업 실패 --- period = {}, baseDate = {}", period, baseDate, e);
      }
    }
  }
}
