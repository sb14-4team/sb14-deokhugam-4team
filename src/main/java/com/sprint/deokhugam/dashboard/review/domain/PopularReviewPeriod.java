package com.sprint.deokhugam.dashboard.review.domain;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;

// baseDate를 기준으로 startAt, endAt 범위를 만든다
public enum PopularReviewPeriod {
  // 전날 00:00 ~ 23:59:59
  DAILY {
    @Override
    public Range rangeOf(LocalDate baseDate) {
      return Range.of(baseDate.minusDays(1), baseDate);
    }
  },
  // 지난주 월요일 00:00 ~ 일요일 23:59:59
  WEEKLY {
    @Override
    public Range rangeOf(LocalDate baseDate) {
      LocalDate thisMonday = baseDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
      return Range.of(thisMonday.minusWeeks(1), thisMonday);
    }
  },
  // 지난달 1일 00:00 ~ 지난달 마지막 날 23:59:59
  MONTHLY {
    @Override
    public Range rangeOf(LocalDate baseDate) {
      LocalDate thisMonthFirst = baseDate.withDayOfMonth(1);
      return Range.of(thisMonthFirst.minusMonths(1), thisMonthFirst);
    }
  },
  // 날짜 상관없이 전체 기간
  ALL_TIME {
    @Override
    public Range rangeOf(LocalDate baseDate) {
      return Range.UNBOUNDED;
    }
  };

  public static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

  public abstract Range rangeOf(LocalDate baseDate);

  // startAt 이상, endAt 미만. null이면 그쪽 경계가 없다
  public record Range(Instant startAt, Instant endAt) {

    static final Range UNBOUNDED = new Range(null, null);

    static Range of(LocalDate startDate, LocalDate endDate) {
      return new Range(
          startDate.atStartOfDay(ZONE).toInstant(), endDate.atStartOfDay(ZONE).toInstant());
    }
  }
}
