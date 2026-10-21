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
  FORBIDDEN(HttpStatus.FORBIDDEN, "REVIEW_FORBIDDEN", "본인이 작성한 리뷰만 수정·삭제할 수 있습니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;
}
