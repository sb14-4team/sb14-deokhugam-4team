package com.sprint.deokhugam.dashboard.book.adapter.batch;

import com.sprint.deokhugam.dashboard.book.application.PopularBookRankingUseCase;
import com.sprint.deokhugam.dashboard.book.domain.RankingPeriod;
import java.time.LocalDate;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/*
 스프링 배치
 - Job -> 하고자 하는 작업
 - Step -> 그 작업안의 과정들
 - tasklet -> 수행 코드 부분
 - JobRepo -> 성공 실패 등의 정보를 관리

 스프링 배치를 쓰는 이유 : 어떤 작업이 성공 실패했는지 추적 및 복구 프로세스를 정의하기 위해서
 - 페리오드 기준으로 잡 단위를 구분한다. 리뷰 유저는 제외 도서만 생각한다.
 - 일 주 월 총 4가지 잡의 트랜잭션으로 롤백 시 해당 잡만 롤백된다.
 - 실패 시 어떤 케이스로 실패하는지 정의해야한다.
   - 공통 이유 실패(인프라 장애) 시 모든 작업을 중지한다. (안함)
   - 이외의 케이스라면 나머지 잡을 진행하도록 결정한다.
*/
@Configuration
public class PopularBookBatchConfig {
  @Bean
  public Job popularBookRankingJob(JobRepository jobRepository, Step popularBookRankingStep) {

    return new JobBuilder("popularBookRankingJob", jobRepository)
        .start(popularBookRankingStep)
        .build();
  }

  @Bean
  @JobScope
  public Step popularBookRankingStep(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      PopularBookRankingUseCase rankingUseCase,
      @Value("#{jobParameters['period']}") RankingPeriod period,
      @Value("#{jobParameters['baseDate']}") LocalDate baseDate) {

    return new StepBuilder("popularBookRankingStep", jobRepository)
        .tasklet(
            (contribution, chunkContext) -> {
              rankingUseCase.calculateRankings(period, baseDate);

              return RepeatStatus.FINISHED;
            },
            transactionManager)
        .build();
  }
}
