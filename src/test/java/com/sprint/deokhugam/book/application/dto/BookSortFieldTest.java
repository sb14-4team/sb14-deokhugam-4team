package com.sprint.deokhugam.book.application.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.common.exception.CommonErrorCode;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BookSortFieldTest {

  // ===== (1) from() : 요청 파라미터 → enum =====
  // (1-1)
  @Test
  @DisplayName("명세의 orderBy 값 네 가지를 enum으로 바꾼다")
  void from() {
    assertThat(BookSortField.from("title")).isEqualTo(BookSortField.TITLE);
    assertThat(BookSortField.from("publishedDate")).isEqualTo(BookSortField.PUBLISHED_DATE);
    assertThat(BookSortField.from("rating")).isEqualTo(BookSortField.RATING);
    assertThat(BookSortField.from("reviewCount")).isEqualTo(BookSortField.REVIEW_COUNT);
  }

  // (1-2)
  @Test
  @DisplayName("명세에 없는 orderBy 값이면 INVALID_INPUT 예외가 발생한다")
  void fromUnknown() {
    assertThatThrownBy(() -> BookSortField.from("author"))
        .isInstanceOf(BusinessException.class)
        .extracting("errorCode")
        .isEqualTo(CommonErrorCode.INVALID_INPUT);
  }

  // ===== (2) parseCursor() : 커서 문자열 → 정렬 기준 타입 =====
  // (2-1)
  @Test
  @DisplayName("정렬 기준에 맞는 타입으로 커서를 되돌린다")
  void parseCursor() {
    assertThat(BookSortField.TITLE.parseCursor("클린 코드")).isEqualTo("클린 코드");
    assertThat(BookSortField.PUBLISHED_DATE.parseCursor("2013-12-24"))
        .isEqualTo(LocalDate.of(2013, 12, 24));
    assertThat(BookSortField.RATING.parseCursor("4.5")).isEqualTo(4.5);
    assertThat(BookSortField.REVIEW_COUNT.parseCursor("12")).isEqualTo(12);
  }

  // (2-2)
  @Test
  @DisplayName("정렬 기준 타입으로 읽을 수 없는 커서면 INVALID_INPUT 예외가 발생한다")
  void parseCursorInvalid() {
    assertThatThrownBy(() -> BookSortField.RATING.parseCursor("abc"))
        .isInstanceOf(BusinessException.class)
        .extracting("errorCode")
        .isEqualTo(CommonErrorCode.INVALID_INPUT);
    assertThatThrownBy(() -> BookSortField.PUBLISHED_DATE.parseCursor("2013/12/24"))
        .isInstanceOf(BusinessException.class)
        .extracting("errorCode")
        .isEqualTo(CommonErrorCode.INVALID_INPUT);
  }
}
