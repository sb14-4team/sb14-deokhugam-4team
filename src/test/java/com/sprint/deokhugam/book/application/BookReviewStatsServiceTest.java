package com.sprint.deokhugam.book.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sprint.deokhugam.book.FakeBookRepositoryPort;
import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.domain.book.exception.BookErrorCode;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BookReviewStatsServiceTest {

  private final FakeBookRepositoryPort bookRepositoryPort = new FakeBookRepositoryPort();
  private final BookReviewStatsService bookReviewStatsService =
      new BookReviewStatsService(bookRepositoryPort);

  private Book savedBook() {
    return bookRepositoryPort.save(
        Book.create(
            "클린 코드", "로버트 C. 마틴", "소개", "인사이트", LocalDate.of(2013, 12, 24), "9788966260959", null));
  }

  // ===== (1) addReview() =====
  // (1-1)
  @Test
  @DisplayName("리뷰가 달리면 도서의 리뷰 수가 늘고 평점은 평균이 된다")
  void addReview() {
    Book book = savedBook();

    bookReviewStatsService.addReview(book.getId(), 5);
    bookReviewStatsService.addReview(book.getId(), 3);

    assertThat(book.getReviewCount()).isEqualTo(2);
    assertThat(book.getRating()).isEqualTo(4.0);
  }

  // (1-2)
  @Test
  @DisplayName("없는 도서면 BOOK_NOT_FOUND")
  void addReviewToMissingBook() {
    assertThatThrownBy(() -> bookReviewStatsService.addReview(UUID.randomUUID(), 5))
        .isInstanceOf(BusinessException.class)
        .extracting("errorCode")
        .isEqualTo(BookErrorCode.NOT_FOUND);
  }

  // (1-3)
  @Test
  @DisplayName("논리 삭제된 도서에도 집계가 반영된다")
  void addReviewToDeletedBook() {
    Book book = savedBook();
    book.softDelete();

    bookReviewStatsService.addReview(book.getId(), 4);

    assertThat(book.getReviewCount()).isEqualTo(1);
    assertThat(book.getRating()).isEqualTo(4.0);
  }

  // ===== (2) removeReview() =====
  // (2-1)
  @Test
  @DisplayName("리뷰가 지워지면 리뷰 수가 줄고 평점을 다시 계산한다")
  void removeReview() {
    Book book = savedBook();
    bookReviewStatsService.addReview(book.getId(), 5);
    bookReviewStatsService.addReview(book.getId(), 3);

    bookReviewStatsService.removeReview(book.getId(), 3);

    assertThat(book.getReviewCount()).isEqualTo(1);
    assertThat(book.getRating()).isEqualTo(5.0);
  }

  // ===== (3) changeReviewRating() =====
  // (3-1)
  @Test
  @DisplayName("리뷰 점수가 바뀌면 리뷰 수는 그대로고 평점만 바뀐다")
  void changeReviewRating() {
    Book book = savedBook();
    bookReviewStatsService.addReview(book.getId(), 2);

    bookReviewStatsService.changeReviewRating(book.getId(), 2, 4);

    assertThat(book.getReviewCount()).isEqualTo(1);
    assertThat(book.getRating()).isEqualTo(4.0);
  }
}
