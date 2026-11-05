package com.sprint.deokhugam.dashboard.book.application.res;

import com.sprint.deokhugam.dashboard.book.domain.PopularBook;
import com.sprint.deokhugam.dashboard.book.domain.RankingPeriod;
import java.time.Instant;
import java.util.UUID;

public record PopularBookResponse(
    UUID id,
    UUID bookId,
    String title,
    String author,
    String thumbnailUrl,
    RankingPeriod period,
    Long rank,
    Double score,
    Long reviewCount,
    Double rating,
    Instant createdAt) {

  public static PopularBookResponse from(PopularBook popularBook) {
    return new PopularBookResponse(
        popularBook.getId(),
        popularBook.getBook().getId(),
        popularBook.getBook().getTitle(),
        popularBook.getBook().getAuthor(),
        popularBook.getBook().getThumbnailUrl(),
        popularBook.getPeriod(),
        popularBook.getRank(),
        popularBook.getScore(),
        popularBook.getReviewCount(),
        popularBook.getAverageRating(),
        popularBook.getCreatedAt());
  }
}
