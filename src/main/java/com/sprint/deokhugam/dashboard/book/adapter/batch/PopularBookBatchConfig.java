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
