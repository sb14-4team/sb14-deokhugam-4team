package com.sprint.deokhugam.dashboard.book.domain;

import java.util.Comparator;
import java.util.List;

/*
 - 점수 내림차순으로 정렬
 - 동점이면 리뷰 수 내림차순, 도서 생성일/ID 오름차순으로 정렬
*/
public class PopularBookRanker {

  public static List<BookRankingCandidate> rank(List<BookRankingCandidate> candidates) {
    return candidates.stream()
        .sorted(
            Comparator.comparingDouble(BookRankingCandidate::score)
                .reversed()
                .thenComparing(
                    Comparator.comparingLong(BookRankingCandidate::reviewCount).reversed())
                .thenComparing(BookRankingCandidate::bookCreatedAt)
                .thenComparing(BookRankingCandidate::bookId))
        .toList();
  }
}
