package com.sprint.deokhugam.dashboard.book.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.common.exception.CommonErrorCode;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PopularBookTest {

  @Test
  void 리뷰수와_평점의_가중치로_점수를_계산해_저장한다() {
    // given
    Book book = mock(Book.class);
    LocalDate baseDate = LocalDate.of(2026, 11, 6);

    // when
    PopularBook ranking = PopularBook.create(book, RankingPeriod.DAILY, baseDate, 10, 4, 1);

    // then
    assertThat(ranking.getBook()).isSameAs(book);
    assertThat(ranking.getPeriod()).isEqualTo(RankingPeriod.DAILY);
    assertThat(ranking.getBaseDate()).isEqualTo(baseDate);
    assertThat(ranking.getReviewCount()).isEqualTo(10);
    assertThat(ranking.getAverageRating()).isEqualTo(4);
    assertThat(ranking.getScore()).isEqualTo(6.4);
    assertThat(ranking.getRank()).isEqualTo(1);
  }

  @ParameterizedTest
  @CsvSource({"0, 3, 1", "1, 0, 1", "1, 6, 1", "1, 3, 0"})
  void 리뷰수_평점_순위가_유효하지_않으면_생성하지_않는다(long reviewCount, double averageRating, long rank) {
    // given
    Book book = mock(Book.class);
    LocalDate baseDate = LocalDate.of(2026, 11, 6);

    // when
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () ->
                PopularBook.create(
                    book, RankingPeriod.DAILY, baseDate, reviewCount, averageRating, rank));

    // then
    assertThat(exception.getErrorCode()).isEqualTo(CommonErrorCode.INVALID_INPUT);
  }
}
