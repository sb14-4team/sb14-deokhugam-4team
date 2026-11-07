package com.sprint.deokhugam.dashboard.book.application.provided.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.common.exception.CommonErrorCode;
import com.sprint.deokhugam.dashboard.book.domain.RankingPeriod;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.data.domain.Sort;

class PopularBookSearchConditionTest {

  @Test
  void 커서가_없으면_첫_페이지_조건을_만든다() {
    // given
    RankingPeriod period = RankingPeriod.DAILY;

    // when
    PopularBookSearchCondition condition =
        PopularBookSearchCondition.toCondition(period, Sort.Direction.ASC, null, null, 20);

    // then
    assertThat(condition.period()).isEqualTo(period);
    assertThat(condition.limit()).isEqualTo(20);
    assertThat(condition.isFirstPage()).isTrue();
    assertThat(condition.isDescending()).isFalse();
  }

  @Test
  void 순위_커서를_숫자로_변환하고_보조커서와_정렬방향을_유지한다() {
    // given
    Instant after = Instant.parse("2026-11-05T15:00:00Z");

    // when
    PopularBookSearchCondition condition =
        PopularBookSearchCondition.toCondition(
            RankingPeriod.WEEKLY, Sort.Direction.DESC, "2", after, 20);

    // then
    assertThat(condition.cursor()).isEqualTo(2L);
    assertThat(condition.after()).isEqualTo(after);
    assertThat(condition.isFirstPage()).isFalse();
    assertThat(condition.isDescending()).isTrue();
  }

  @ParameterizedTest
  @ValueSource(strings = {"text", "0", "-1"})
  void 커서가_양의_정수가_아니면_거절한다(String cursor) {
    // given
    Instant after = Instant.parse("2026-11-05T15:00:00Z");

    // when
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () ->
                PopularBookSearchCondition.toCondition(
                    RankingPeriod.DAILY, Sort.Direction.ASC, cursor, after, 20));

    // then
    assertThat(exception.getErrorCode()).isEqualTo(CommonErrorCode.INVALID_INPUT);
  }

  @Test
  void 커서만_있고_보조커서가_없으면_거절한다() {
    // given
    String cursor = "2";

    // when
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () ->
                PopularBookSearchCondition.toCondition(
                    RankingPeriod.DAILY, Sort.Direction.ASC, cursor, null, 20));

    // then
    assertThat(exception.getErrorCode()).isEqualTo(CommonErrorCode.INVALID_INPUT);
  }
}
