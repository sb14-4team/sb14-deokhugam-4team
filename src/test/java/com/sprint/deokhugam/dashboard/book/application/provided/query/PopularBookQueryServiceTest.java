package com.sprint.deokhugam.dashboard.book.application.provided.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.common.pagination.CursorPageResponse;
import com.sprint.deokhugam.dashboard.book.application.required.PopularBookQueryPort;
import com.sprint.deokhugam.dashboard.book.application.res.PopularBookResponse;
import com.sprint.deokhugam.dashboard.book.domain.PopularBook;
import com.sprint.deokhugam.dashboard.book.domain.RankingPeriod;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PopularBookQueryServiceTest {

  @Mock PopularBookQueryPort queryPort;
  @InjectMocks PopularBookQueryService service;

  @Test
  void 집계_결과가_없으면_빈_페이지를_반환한다() {
    // given
    PopularBookSearchCondition condition =
        PopularBookSearchCondition.toCondition(
            RankingPeriod.DAILY, Sort.Direction.ASC, null, null, 2);
    when(queryPort.search(condition)).thenReturn(List.of());

    // when
    CursorPageResponse<PopularBookResponse> result =
        service.searchPopularBooksByPagination(condition);

    // then
    assertThat(result.content()).isEmpty();
    assertThat(result.size()).isZero();
    assertThat(result.hasNext()).isFalse();
    assertThat(result.nextCursor()).isNull();
    assertThat(result.nextAfter()).isNull();
  }

  @Test
  void 도서_정보와_집계값을_응답으로_변환한다() {
    // given
    PopularBookSearchCondition condition =
        PopularBookSearchCondition.toCondition(
            RankingPeriod.DAILY, Sort.Direction.ASC, null, null, 2);
    PopularBook ranking = ranking(1);
    when(queryPort.search(condition)).thenReturn(List.of(ranking));

    // when
    CursorPageResponse<PopularBookResponse> result =
        service.searchPopularBooksByPagination(condition);

    // then
    assertThat(result.content()).hasSize(1);
    PopularBookResponse response = result.content().getFirst();
    assertThat(response.id()).isEqualTo(ranking.getId());
    assertThat(response.bookId()).isEqualTo(ranking.getBook().getId());
    assertThat(response.title()).isEqualTo("도서 1");
    assertThat(response.author()).isEqualTo("저자");
    assertThat(response.thumbnailUrl()).isEqualTo("https://example.com/cover.png");
    assertThat(response.period()).isEqualTo(RankingPeriod.DAILY);
    assertThat(response.rank()).isEqualTo(1L);
    assertThat(response.reviewCount()).isEqualTo(10L);
    assertThat(response.rating()).isEqualTo(4.0);
    assertThat(response.score()).isEqualTo(6.4);
    assertThat(response.createdAt()).isEqualTo(ranking.getCreatedAt());
    assertThat(result.hasNext()).isFalse();
    assertThat(result.nextCursor()).isNull();
  }

  @Test
  void 다음_페이지가_있으면_마지막으로_노출한_순위와_생성일을_커서로_준다() {
    // given
    PopularBookSearchCondition condition =
        PopularBookSearchCondition.toCondition(
            RankingPeriod.DAILY, Sort.Direction.ASC, null, null, 2);
    PopularBook first = ranking(1);
    PopularBook second = ranking(2);
    PopularBook extra = ranking(3);
    when(queryPort.search(condition)).thenReturn(List.of(first, second, extra));

    // when
    CursorPageResponse<PopularBookResponse> result =
        service.searchPopularBooksByPagination(condition);

    // then
    assertThat(result.content()).extracting(PopularBookResponse::rank).containsExactly(1L, 2L);
    assertThat(result.size()).isEqualTo(2);
    assertThat(result.hasNext()).isTrue();
    assertThat(result.nextCursor()).isEqualTo("2");
    assertThat(result.nextAfter()).isEqualTo(second.getCreatedAt());
  }

  private PopularBook ranking(long rank) {
    Book book =
        Book.create("도서 " + rank, "저자", null, null, null, null, "https://example.com/cover.png");
    ReflectionTestUtils.setField(book, "id", new UUID(0, rank));
    PopularBook ranking =
        PopularBook.create(book, RankingPeriod.DAILY, LocalDate.of(2026, 11, 6), 10, 4, rank);
    ReflectionTestUtils.setField(ranking, "id", new UUID(1, rank));
    ReflectionTestUtils.setField(ranking, "createdAt", Instant.parse("2026-11-05T15:00:00Z"));
    return ranking;
  }
}
