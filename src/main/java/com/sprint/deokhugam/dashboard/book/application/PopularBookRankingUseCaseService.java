package com.sprint.deokhugam.dashboard.book.application;

import com.sprint.deokhugam.dashboard.book.domain.BookRankingCandidate;
import com.sprint.deokhugam.dashboard.book.domain.PopularBookRanker;
import com.sprint.deokhugam.dashboard.book.domain.RankingPeriod;
import com.sprint.deokhugam.dashboard.book.domain.RankingRange;
import com.sprint.deokhugam.review.application.dto.res.BookReviewStatsResponse;
import com.sprint.deokhugam.review.application.provided.query.ReviewAggregationRange;
import com.sprint.deokhugam.review.application.provided.query.ReviewFinder;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class PopularBookRankingUseCaseService implements PopularBookRankingUseCase {
  private final ReviewFinder reviewFinder;

  /*
   1. 집계 결과 받아옴
   2. 주기 별 계산 및 순위 정렬하여 목록 생성
   3. 저장
  */
  @Override
  @Transactional
  public void calculateRankings(RankingPeriod period, LocalDate baseDate) {
    log.info(" ==== BOOK_RANKING_STARTED ===== period = {}, baseDate = {}", period, baseDate);
    try {
      ReviewAggregationRange range =
          toReviewAggregationRange(period.rangeOf(baseDate)); // 대시보드 도메인 // 리뷰 타입에 의존 분리 위함
      List<BookReviewStatsResponse> bookStatsList = reviewFinder.aggregateByBook(range);
      List<BookRankingCandidate> candidates = toCandidates(bookStatsList);
      List<BookRankingCandidate> sortedCandidates = PopularBookRanker.rank(candidates);
    } catch (RuntimeException e) {
      log.error(" ==== BOOK_RANKING_FAILED ==== period = {} baseDate = {}", period, baseDate, e);
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
}
