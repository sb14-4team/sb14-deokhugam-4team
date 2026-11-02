package com.sprint.deokhugam.dashboard.book.application;

import com.sprint.deokhugam.dashboard.book.domain.RankingPeriod;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PopularBookRankingUseCaseService implements PopularBookRankingUseCase {

  @Override
  @Transactional
  public void calculateRankings(RankingPeriod period, LocalDate baseDate) {}
}
