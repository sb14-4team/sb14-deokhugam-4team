package com.sprint.deokhugam.dashboard.book.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.dashboard.book.application.required.PopularBookRepositoryPort;
import com.sprint.deokhugam.dashboard.book.domain.PopularBook;
import com.sprint.deokhugam.dashboard.book.domain.RankingPeriod;
import com.sprint.deokhugam.review.application.dto.res.BookReviewStatsResponse;
import com.sprint.deokhugam.review.application.provided.query.ReviewAggregationRange;
import com.sprint.deokhugam.review.application.provided.query.ReviewFinder;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PopularBookRankingUseCaseServiceTest {

  @Mock ReviewFinder reviewFinder;
  @Mock PopularBookRepositoryPort repositoryPort;
  @InjectMocks PopularBookRankingUseCaseService service;
  @Captor ArgumentCaptor<List<PopularBook>> rankingsCaptor;

  private final LocalDate baseDate = LocalDate.of(2026, 11, 6);

  @Test
  void 상위_20개_도서에_순위를_매겨_원래_도서와_집계값을_저장한다() {
    // given
    List<BookReviewStatsResponse> stats =
        IntStream.rangeClosed(1, 21).mapToObj(this::stats).toList();
    when(reviewFinder.aggregateByBook(any())).thenReturn(stats);

    // when
    service.calculateRankings(RankingPeriod.DAILY, baseDate);

    // then
    verify(repositoryPort).saveAll(rankingsCaptor.capture());
    List<PopularBook> rankings = rankingsCaptor.getValue();
    assertThat(rankings).hasSize(20);
    for (int index = 0; index < rankings.size(); index++) {
      PopularBook ranking = rankings.get(index);
      BookReviewStatsResponse expected = stats.get(20 - index);
      assertThat(ranking.getBook()).isSameAs(expected.book());
      assertThat(ranking.getReviewCount()).isEqualTo(expected.reviewCount());
      assertThat(ranking.getAverageRating()).isEqualTo(expected.averageRating());
      assertThat(ranking.getRank()).isEqualTo(index + 1L);
      assertThat(ranking.getPeriod()).isEqualTo(RankingPeriod.DAILY);
      assertThat(ranking.getBaseDate()).isEqualTo(baseDate);
    }
    assertThat(rankings)
        .extracting(ranking -> ranking.getBook().getId())
        .doesNotContain(new UUID(0, 1));
  }

  @ParameterizedTest
  @EnumSource(RankingPeriod.class)
  void 집계가_비어있으면_기간에_맞는_범위로_조회하고_저장하지_않는다(RankingPeriod period) {
    // given
    ReviewAggregationRange expected =
        switch (period) {
          case DAILY ->
              ReviewAggregationRange.between(
                  Instant.parse("2026-11-04T15:00:00Z"), Instant.parse("2026-11-05T15:00:00Z"));
          case WEEKLY ->
              ReviewAggregationRange.between(
                  Instant.parse("2026-10-25T15:00:00Z"), Instant.parse("2026-11-01T15:00:00Z"));
          case MONTHLY ->
              ReviewAggregationRange.between(
                  Instant.parse("2026-09-30T15:00:00Z"), Instant.parse("2026-10-31T15:00:00Z"));
          case ALL_TIME -> ReviewAggregationRange.allTime();
        };
    when(reviewFinder.aggregateByBook(expected)).thenReturn(List.of());

    // when
    service.calculateRankings(period, baseDate);

    // then
    verify(reviewFinder).aggregateByBook(expected);
    verifyNoInteractions(repositoryPort);
  }

  @Test
  void 리뷰_집계가_실패하면_저장하지_않고_예외를_전달한다() {
    // given
    RuntimeException failure = new IllegalStateException("리뷰 집계 실패");
    when(reviewFinder.aggregateByBook(any())).thenThrow(failure);

    // when
    RuntimeException exception =
        assertThrows(
            RuntimeException.class, () -> service.calculateRankings(RankingPeriod.DAILY, baseDate));

    // then
    assertThat(exception).isSameAs(failure);
    verifyNoInteractions(repositoryPort);
  }

  @Test
  void 순위_저장이_실패하면_예외를_삼키지_않는다() {
    // given
    RuntimeException failure = new IllegalStateException("순위 저장 실패");
    when(reviewFinder.aggregateByBook(any())).thenReturn(List.of(stats(1)));
    doThrow(failure).when(repositoryPort).saveAll(anyList());

    // when
    RuntimeException exception =
        assertThrows(
            RuntimeException.class, () -> service.calculateRankings(RankingPeriod.DAILY, baseDate));

    // then
    assertThat(exception).isSameAs(failure);
  }

  private BookReviewStatsResponse stats(int index) {
    Book book = Book.create("도서 " + index, "저자", null, null, null, null, null);
    ReflectionTestUtils.setField(book, "id", new UUID(0, index));
    ReflectionTestUtils.setField(book, "createdAt", Instant.parse("2026-01-01T00:00:00Z"));
    return new BookReviewStatsResponse(book, index, 4);
  }
}
