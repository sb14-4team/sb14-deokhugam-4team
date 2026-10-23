package com.sprint.deokhugam.domain.review.exception;

import com.sprint.deokhugam.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ReviewErrorCode implements ErrorCode {
  NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW_NOT_FOUND", "리뷰 정보가 없습니다."),
  ALREADY_EXISTS(HttpStatus.CONFLICT, "REVIEW_ALREADY_EXISTS", "이미 이 도서에 작성한 리뷰가 있습니다."),
  FORBIDDEN(HttpStatus.FORBIDDEN, "REVIEW_FORBIDDEN", "본인이 작성한 리뷰만 수정·삭제할 수 있습니다."),
  INVALID_RATING(HttpStatus.BAD_REQUEST, "REVIEW_INVALID_RATING", "리뷰 평점은 1~5점만 가능합니다."),
  INVALID_CONTENT(HttpStatus.BAD_REQUEST, "REVIEW_INVALID_CONTENT", "리뷰 내용은 200자 이하여야 합니다."),
  INVALID_USER(HttpStatus.BAD_REQUEST, "REVIEW_INVALID_USER", "리뷰 작성자가 필요합니다."),
  INVALID_BOOK(HttpStatus.BAD_REQUEST, "REVIEW_INVALID_BOOK", "리뷰 도서가 필요합니다."),
  LIKE_COUNT_UNDERFLOW(
      HttpStatus.CONFLICT, "REVIEW_LIKE_COUNT_UNDERFLOW", "좋아요 수는 0 미만이 될 수 없습니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;
}
