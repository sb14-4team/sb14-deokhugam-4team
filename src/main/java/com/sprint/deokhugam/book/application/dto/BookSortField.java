package com.sprint.deokhugam.book.application.dto;

import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.common.exception.CommonErrorCode;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

// 도서 목록 정렬 기준. orderBy 파라미터 ↔ enum ↔ 커서 값 변환을 이 한 곳에서 처리한다
public enum BookSortField {
  TITLE("title"),
  PUBLISHED_DATE("publishedDate"),
  RATING("rating"),
  REVIEW_COUNT("reviewCount");

  // 요청 파라미터에 오는 글자 (명세: title | publishedDate | rating | reviewCount)
  private final String value;

  BookSortField(String value) {
    this.value = value;
  }

  // "publishedDate" → PUBLISHED_DATE. 명세에 없는 값이면 400
  public static BookSortField from(String value) {
    for (BookSortField field : values()) {
      if (field.value.equals(value)) {
        return field;
      }
    }
    throw new BusinessException(CommonErrorCode.INVALID_INPUT, "orderBy=" + value);
  }

  // 응답의 nextCursor에 넣을 값 = 마지막 도서의 정렬 기준 값
  public Object cursorOf(BookDto book) {
    return switch (this) {
      case TITLE -> book.title();
      case PUBLISHED_DATE -> book.publishedDate();
      case RATING -> book.rating();
      case REVIEW_COUNT -> book.reviewCount();
    };
  }

  // 요청의 cursor 문자열을 정렬 기준의 원래 타입으로 되돌린다. 형식이 틀리면 400
  public Object parseCursor(String cursor) {
    try {
      return switch (this) {
        case TITLE -> cursor;
        case PUBLISHED_DATE -> LocalDate.parse(cursor);
        case RATING -> Double.parseDouble(cursor);
        case REVIEW_COUNT -> Integer.parseInt(cursor);
      };
    } catch (NumberFormatException | DateTimeParseException e) {
      throw new BusinessException(CommonErrorCode.INVALID_INPUT, "cursor=" + cursor);
    }
  }
}
