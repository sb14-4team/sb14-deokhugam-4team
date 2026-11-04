package com.sprint.deokhugam.dashboard.book.application;

import com.sprint.deokhugam.dashboard.book.domain.RankingPeriod;
import java.time.LocalDate;

public interface PopularBookRankingUseCase {
  void calculateRankings(RankingPeriod period, LocalDate baseDate);
}
