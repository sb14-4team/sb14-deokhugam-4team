package com.sprint.deokhugam.book.application.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.common.exception.CommonErrorCode;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;

class BookSearchConditionTest {

  // (1)
  @Test
  @DisplayName("공백만 있는 키워드는 없는 것으로, 앞뒤 공백은 잘라서 담는다")
  void keywordNormalized() {
    BookSearchCondition blank =
        new BookSearchCondition("   ", BookSortField.TITLE, Sort.Direction.DESC, null, null);
    BookSearchCondition padded =
        new BookSearchCondition(" 클린 ", BookSortField.TITLE, Sort.Direction.DESC, null, null);

    assertThat(blank.keyword()).isNull();
    assertThat(padded.keyword()).isEqualTo("클린");
  }

  // (2)
  @Test
  @DisplayName("cursor와 after 중 하나만 오면 INVALID_INPUT 예외가 발생한다")
  void cursorWithoutAfter() {
    assertThatThrownBy(
            () ->
                new BookSearchCondition(
                    null, BookSortField.TITLE, Sort.Direction.DESC, "클린 코드", null))
        .isInstanceOf(BusinessException.class)
        .extracting("errorCode")
        .isEqualTo(CommonErrorCode.INVALID_INPUT);
    assertThatThrownBy(
            () ->
                new BookSearchCondition(
                    null, BookSortField.TITLE, Sort.Direction.DESC, null, Instant.now()))
        .isInstanceOf(BusinessException.class)
        .extracting("errorCode")
        .isEqualTo(CommonErrorCode.INVALID_INPUT);
  }

  // (3)
  @Test
  @DisplayName("정렬 기준 타입으로 읽을 수 없는 cursor면 만들 때 INVALID_INPUT 예외가 발생한다")
  void malformedCursor() {
    assertThatThrownBy(
            () ->
                new BookSearchCondition(
                    null, BookSortField.RATING, Sort.Direction.DESC, "높음", Instant.now()))
        .isInstanceOf(BusinessException.class)
        .extracting("errorCode")
        .isEqualTo(CommonErrorCode.INVALID_INPUT);
  }
}
