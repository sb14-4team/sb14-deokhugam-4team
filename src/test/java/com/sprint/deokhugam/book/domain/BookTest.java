package com.sprint.deokhugam.book.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.common.exception.CommonErrorCode;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BookTest {

  private Book createBook() {
    return Book.create("클린 코드", "로버트 C. 마틴", "소개", "인사이트", LocalDate.of(2013, 12, 24), null, null);
  }

  // ===== (1) Book.create() =====
  // (1-1)
  @Test
  @DisplayName("새로 만든 도서는 리뷰 수와 평점이 0이다")
  void create() {
    Book book = createBook();

    assertThat(book.getReviewCount()).isZero();
    assertThat(book.getRating()).isZero();
  }

  // (1-2)
  @Test
  @DisplayName("제목이 최대 길이면 만들 수 있다")
  void createWithMaxLengthTitle() {
    String title = "가".repeat(Book.TITLE_MAX_LENGTH);

    Book book = Book.create(title, "저자", "소개", "출판사", LocalDate.of(2020, 1, 1), null, null);

    assertThat(book.getTitle()).hasSize(Book.TITLE_MAX_LENGTH);
  }

  // (1-3)
  @Test
  @DisplayName("제목이 최대 길이를 넘으면 만들 때 예외가 발생한다")
  void createWithTooLongTitle() {
    String title = "가".repeat(Book.TITLE_MAX_LENGTH + 1);

    assertThatThrownBy(
            () -> Book.create(title, "저자", "소개", "출판사", LocalDate.of(2020, 1, 1), null, null))
        .isInstanceOf(BusinessException.class)
        .extracting("errorCode")
        .isEqualTo(CommonErrorCode.INVALID_INPUT);
  }

  // ===== (2) update() =====
  // (2-1)
  @Test
  @DisplayName("수정하면 ISBN을 뺀 기본 정보가 바뀐다")
  void update() {
    Book book =
        Book.create(
            "제목", "저자", "소개", "출판사", LocalDate.of(2020, 1, 1), Isbn.of("9788966260959"), null);

    book.update("새 제목", "새 저자", "새 소개", "새 출판사", LocalDate.of(2021, 2, 2));

    assertThat(book.getTitle()).isEqualTo("새 제목");
    assertThat(book.getPublishedDate()).isEqualTo(LocalDate.of(2021, 2, 2));
    assertThat(book.getIsbn()).isEqualTo(Isbn.of("9788966260959"));
  }

  // (2-2)
  @Test
  @DisplayName("출판사가 최대 길이를 넘으면 수정할 때 예외가 발생하고 값은 그대로다")
  void updateWithTooLongPublisher() {
    Book book = createBook();
    String publisher = "가".repeat(Book.PUBLISHER_MAX_LENGTH + 1);

    assertThatThrownBy(
            () -> book.update("새 제목", "새 저자", "새 소개", publisher, LocalDate.of(2021, 2, 2)))
        .isInstanceOf(BusinessException.class);
    assertThat(book.getTitle()).isEqualTo("클린 코드");
  }

  // ===== (3) addReview() =====
  // (3-1)
  @Test
  @DisplayName("리뷰를 추가하면 리뷰 수가 늘고 평점은 평균이 된다")
  void addReview() {
    Book book = createBook();

    book.addReview(5);
    book.addReview(4);

    assertThat(book.getReviewCount()).isEqualTo(2);
    assertThat(book.getRating()).isEqualTo(4.5);
  }

  // ===== (4) removeReview() =====
  // (4-1)
  @Test
  @DisplayName("리뷰를 모두 삭제하면 평점이 0이 된다")
  void removeAllReviews() {
    Book book = createBook();
    book.addReview(3);

    book.removeReview(3);

    assertThat(book.getReviewCount()).isZero();
    assertThat(book.getRating()).isZero();
  }

  // ===== (5) changeReviewScore() =====
  // (5-1)
  @Test
  @DisplayName("리뷰 점수를 바꾸면 리뷰 수는 그대로고 평점만 바뀐다")
  void changeReviewScore() {
    Book book = createBook();
    book.addReview(2);
    book.addReview(4);

    book.changeReviewScore(2, 5);

    assertThat(book.getReviewCount()).isEqualTo(2);
    assertThat(book.getRating()).isEqualTo(4.5);
  }
}
