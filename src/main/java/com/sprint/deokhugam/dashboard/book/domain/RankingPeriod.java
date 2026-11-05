package com.sprint.deokhugam.dashboard.book.domain;

import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.common.exception.CommonErrorCode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;

public enum RankingPeriod {
  DAILY,
  WEEKLY,
  MONTHLY,
  ALL_TIME;

  private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

  public RankingRange rangeOf(LocalDate baseDate) {
    if (baseDate == null) {
      throw new BusinessException(CommonErrorCode.INVALID_INPUT);
    }

    return switch (this) {
      case DAILY -> between(baseDate.minusDays(1), baseDate);
      case WEEKLY -> {
        LocalDate thisMonday = baseDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        yield between(thisMonday.minusWeeks(1), thisMonday);
      }
      case MONTHLY -> {
        LocalDate thisMonthFirst = baseDate.withDayOfMonth(1);
        yield between(thisMonthFirst.minusMonths(1), thisMonthFirst);
      }
      case ALL_TIME -> new RankingRange.AllTime();
    };
  }

  private static RankingRange.Between between(LocalDate startAt, LocalDate endAt) {
    return new RankingRange.Between(
        startAt.atStartOfDay(SEOUL).toInstant(), endAt.atStartOfDay(SEOUL).toInstant());
  }
}
