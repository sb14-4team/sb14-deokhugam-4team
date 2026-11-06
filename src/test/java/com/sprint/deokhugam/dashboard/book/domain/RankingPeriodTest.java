package com.sprint.deokhugam.dashboard.book.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.common.exception.CommonErrorCode;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RankingPeriodTest {

  @ParameterizedTest
  @CsvSource({
    "DAILY, 2026-01-01, 2025-12-30T15:00:00Z, 2025-12-31T15:00:00Z",
    "WEEKLY, 2026-11-09, 2026-11-01T15:00:00Z, 2026-11-08T15:00:00Z",
    "WEEKLY, 2026-11-06, 2026-10-25T15:00:00Z, 2026-11-01T15:00:00Z",
    "MONTHLY, 2024-03-01, 2024-01-31T15:00:00Z, 2024-02-29T15:00:00Z",
    "MONTHLY, 2026-11-06, 2026-09-30T15:00:00Z, 2026-10-31T15:00:00Z"
  })
  void 서울_자정_기준으로_이전_일간_주간_월간_범위를_계산한다(
      RankingPeriod period, LocalDate baseDate, Instant expectedStart, Instant expectedEnd) {
    // given
    RankingRange expected = new RankingRange.Between(expectedStart, expectedEnd);

    // when
    RankingRange result = period.rangeOf(baseDate);

    // then
    assertThat(result).isEqualTo(expected);
  }

  @Test
  void 역대는_기간_제한이_없는_범위를_반환한다() {
    // given
    LocalDate baseDate = LocalDate.of(2026, 11, 6);

    // when
    RankingRange result = RankingPeriod.ALL_TIME.rangeOf(baseDate);

    // then
    assertThat(result).isInstanceOf(RankingRange.AllTime.class);
  }

  @Test
  void 기준일이_없으면_범위를_계산할_수_없다() {
    // given
    LocalDate baseDate = null;

    // when
    BusinessException exception =
        assertThrows(BusinessException.class, () -> RankingPeriod.DAILY.rangeOf(baseDate));

    // then
    assertThat(exception.getErrorCode()).isEqualTo(CommonErrorCode.INVALID_INPUT);
  }
}
