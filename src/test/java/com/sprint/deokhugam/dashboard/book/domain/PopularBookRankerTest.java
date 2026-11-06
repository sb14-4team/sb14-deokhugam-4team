package com.sprint.deokhugam.dashboard.book.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class PopularBookRankerTest {

  @Test
  void 점수_리뷰수_생성일_ID_순서로_동점을_구분한다() {
    // given
    Instant older = Instant.parse("2026-10-01T00:00:00Z");
    Instant newer = Instant.parse("2026-10-02T00:00:00Z");
    BookRankingCandidate highestScore = BookRankingCandidate.of(new UUID(0, 5), newer, 10, 5);
    BookRankingCandidate olderSmallId = BookRankingCandidate.of(new UUID(0, 1), older, 6, 3);
    BookRankingCandidate olderLargeId = BookRankingCandidate.of(new UUID(0, 2), older, 6, 3);
    BookRankingCandidate newerBook = BookRankingCandidate.of(new UUID(0, 3), newer, 6, 3);
    BookRankingCandidate fewerReviews = BookRankingCandidate.of(new UUID(0, 4), older, 3, 5);
    List<BookRankingCandidate> candidates =
        List.of(fewerReviews, newerBook, olderLargeId, highestScore, olderSmallId);
    assertThat(fewerReviews.score()).isEqualTo(olderSmallId.score());

    // when
    List<BookRankingCandidate> result = PopularBookRanker.sortRank(candidates);

    // then
    assertThat(result)
        .containsExactly(highestScore, olderSmallId, olderLargeId, newerBook, fewerReviews);
    assertThat(candidates)
        .containsExactly(fewerReviews, newerBook, olderLargeId, highestScore, olderSmallId);
  }

  @Test
  void 후보가_없으면_빈_순위_목록을_반환한다() {
    // given
    List<BookRankingCandidate> candidates = List.of();

    // when
    List<BookRankingCandidate> result = PopularBookRanker.sortRank(candidates);

    // then
    assertThat(result).isEmpty();
  }
}
