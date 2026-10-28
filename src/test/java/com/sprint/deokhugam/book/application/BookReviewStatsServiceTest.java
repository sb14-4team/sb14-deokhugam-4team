/*package com.sprint.deokhugam.book.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.deokhugam.book.domain.Book;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BookReviewStatsServiceTest {

  private final BookReviewStatsService bookReviewStatsService = new BookReviewStatsService();

  private Book book() {
    return Book.create(
        "클린 코드", "로버트 C. 마틴", "소개", "인사이트", LocalDate.of(2013, 12, 24), "9788966260959", null);
  }

  // ===== (1) addReview() =====
  // (1-1)
  @Test
  @DisplayName("리뷰가 달리면 도서의 리뷰 수가 늘고 평점은 평균이 된다")
  void addReview() {
    Book book = book();

    bookReviewStatsService.addReview(book, 5);
    bookReviewStatsService.addReview(book, 3);

    assertThat(book.getReviewCount()).isEqualTo(2);
    assertThat(book.getRating()).isEqualTo(4.0);
  }

  // ===== (2) removeReview() =====
  // (2-1)
  @Test
  @DisplayName("리뷰가 지워지면 리뷰 수가 줄고 평점을 다시 계산한다")
  void removeReview() {
    Book book = book();
    bookReviewStatsService.addReview(book, 5);
    bookReviewStatsService.addReview(book, 3);

    bookReviewStatsService.removeReview(book, 3);

    assertThat(book.getReviewCount()).isEqualTo(1);
    assertThat(book.getRating()).isEqualTo(5.0);
  }

  // ===== (3) changeReviewRating() =====
  // (3-1)
  @Test
  @DisplayName("리뷰 점수가 바뀌면 리뷰 수는 그대로고 평점만 바뀐다")
  void changeReviewRating() {
    Book book = book();
    bookReviewStatsService.addReview(book, 2);

    bookReviewStatsService.changeReviewRating(book, 2, 4);

    assertThat(book.getReviewCount()).isEqualTo(1);
    assertThat(book.getRating()).isEqualTo(4.0);
  }
}
*/
