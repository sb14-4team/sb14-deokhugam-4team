package com.sprint.deokhugam.dashboard.book.application;

import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.dashboard.book.application.required.PopularBookRepositoryPort;
import com.sprint.deokhugam.dashboard.book.domain.BookRankingCandidate;
import com.sprint.deokhugam.dashboard.book.domain.PopularBook;
import com.sprint.deokhugam.dashboard.book.domain.PopularBookRanker;
import com.sprint.deokhugam.dashboard.book.domain.RankingPeriod;
import com.sprint.deokhugam.dashboard.book.domain.RankingRange;
import com.sprint.deokhugam.review.application.dto.res.BookReviewStatsResponse;
import com.sprint.deokhugam.review.application.provided.query.ReviewAggregationRange;
import com.sprint.deokhugam.review.application.provided.query.ReviewFinder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PopularBookRankingUseCaseService implements PopularBookRankingUseCase {
  private static final int MAX_RANK = 20;

  private final ReviewFinder reviewFinder;

  private final PopularBookRepositoryPort popularBookRepositoryPort;

  /*
   1. 집계 결과 받아옴
   2. 주기 별 계산 및 순위 정렬하여 목록 생성
   3. 저장
  */
  @Override
  @Transactional
  public void calculateRankings(RankingPeriod period, LocalDate baseDate) {
    try {
      // 집계 조히
      ReviewAggregationRange range =
          toReviewAggregationRange(period.rangeOf(baseDate)); // 대시보드 도메인 // 리뷰 타입에 의존 분리 위함
      List<BookReviewStatsResponse> bookStatsList = reviewFinder.aggregateByBook(range);
      if (bookStatsList.isEmpty()) {
        log.info(" 집계 결과 없음 ");
        return;
      }

      // 정렬 시퀀스
      List<BookRankingCandidate> candidates = toCandidates(bookStatsList);
      List<BookRankingCandidate> sortedCandidates =
          PopularBookRanker.sortRank(candidates).stream().limit(MAX_RANK).toList();
      List<PopularBook> rankings = toRankings(bookStatsList, sortedCandidates, period, baseDate);

      popularBookRepositoryPort.saveAll(rankings);
    } catch (RuntimeException e) {
      log.error(" ==== 도서 순위 집계 실패 ==== period = {} baseDate = {}", period, baseDate, e);
      throw e;
    }
  }

  private ReviewAggregationRange toReviewAggregationRange(RankingRange range) {
    return switch (range) {
      case RankingRange.Between r -> ReviewAggregationRange.between(r.startAt(), r.endAt());
      case RankingRange.AllTime ignored -> ReviewAggregationRange.allTime();
    };
  }

  private List<BookRankingCandidate> toCandidates(List<BookReviewStatsResponse> statsList) {
    return statsList.stream()
        .map(
            stats ->
                BookRankingCandidate.of(
                    stats.book().getId(),
                    stats.book().getCreatedAt(),
                    stats.reviewCount(),
                    stats.averageRating()))
        .toList();
  }

  private List<PopularBook> toRankings(
      List<BookReviewStatsResponse> bookStatsList,
      List<BookRankingCandidate> sortedCandidates,
      RankingPeriod period,
      LocalDate baseDate) {
    Map<UUID, Book> booksById =
        bookStatsList.stream()
            .collect(
                Collectors.toMap(stats -> stats.book().getId(), BookReviewStatsResponse::book));

    List<PopularBook> rankings = new ArrayList<>();

    for (int index = 0; index < sortedCandidates.size(); index++) {
      BookRankingCandidate candidate = sortedCandidates.get(index);

      rankings.add(
          PopularBook.create(
              booksById.get(candidate.bookId()),
              period,
              baseDate,
              candidate.reviewCount(),
              candidate.averageRating(),
              index + 1L));
    }

    return rankings;
  }
}
