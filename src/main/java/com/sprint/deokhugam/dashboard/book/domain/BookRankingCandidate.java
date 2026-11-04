package com.sprint.deokhugam.dashboard.book.domain;

import java.time.Instant;
import java.util.UUID;

public record BookRankingCandidate(
    UUID bookId, Instant bookCreatedAt, long reviewCount, double averageRating) {
  public static BookRankingCandidate of(
      UUID bookId, Instant bookCreatedAt, long reviewCount, double averageRating) {
    return new BookRankingCandidate(bookId, bookCreatedAt, reviewCount, averageRating);
  }

  public double score() {
    return PopularBook.calculateScore(reviewCount, averageRating);
  }
}
