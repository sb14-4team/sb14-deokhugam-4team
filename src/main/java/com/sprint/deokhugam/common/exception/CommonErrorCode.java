package com.sprint.deokhugam.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

// 특정 도메인에 속하지 않는 오류
@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
  INVALID_INPUT(HttpStatus.BAD_REQUEST, "COMMON_INVALID_INPUT", "요청 값이 올바르지 않습니다."),
  MISSING_HEADER(HttpStatus.BAD_REQUEST, "COMMON_MISSING_HEADER", "필수 요청 헤더가 없습니다."),
  FILE_TOO_LARGE(HttpStatus.BAD_REQUEST, "COMMON_FILE_TOO_LARGE", "파일 크기가 너무 큽니다."),
  NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_NOT_FOUND", "요청한 주소를 찾을 수 없습니다."),
  METHOD_NOT_ALLOWED(
      HttpStatus.METHOD_NOT_ALLOWED, "COMMON_METHOD_NOT_ALLOWED", "지원하지 않는 HTTP 메서드입니다."),
  INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_INTERNAL_ERROR", "서버 내부 오류가 발생했습니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;
}
