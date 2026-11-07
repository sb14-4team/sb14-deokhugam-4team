package com.sprint.deokhugam.dashboard.review.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PopularReviewPeriodTest {

  private final LocalDate baseDate = LocalDate.of(2026, 10, 29);

  @Test
  @DisplayName("일간은 전날 하루")
  void rangeOf_daily_previousDay() {
    PopularReviewPeriod.Range range = PopularReviewPeriod.DAILY.rangeOf(baseDate);

    assertThat(range.startAt()).isEqualTo(Instant.parse("2026-10-27T15:00:00Z"));
    assertThat(range.endAt()).isEqualTo(Instant.parse("2026-10-28T15:00:00Z"));
  }

  @Test
  @DisplayName("주간은 지난주 월요일부터 일요일까지")
  void rangeOf_weekly_lastMondayToSunday() {
    PopularReviewPeriod.Range range = PopularReviewPeriod.WEEKLY.rangeOf(baseDate);

    assertThat(range.startAt()).isEqualTo(Instant.parse("2026-10-18T15:00:00Z"));
    assertThat(range.endAt()).isEqualTo(Instant.parse("2026-10-25T15:00:00Z"));
  }

  @Test
  @DisplayName("월간은 지난달 1일부터 말일까지")
  void rangeOf_monthly_lastMonthFirstToLastDay() {
    PopularReviewPeriod.Range range = PopularReviewPeriod.MONTHLY.rangeOf(baseDate);

    assertThat(range.startAt()).isEqualTo(Instant.parse("2026-08-31T15:00:00Z"));
    assertThat(range.endAt()).isEqualTo(Instant.parse("2026-09-30T15:00:00Z"));
  }

  @Test
  @DisplayName("역대는 기간 제한이 없음")
  void rangeOf_allTime_noLimit() {
    PopularReviewPeriod.Range range = PopularReviewPeriod.ALL_TIME.rangeOf(baseDate);

    assertThat(range.startAt()).isNull();
    assertThat(range.endAt()).isNull();
  }
}
