package com.sprint.deokhugam.common.exception.domain;

import com.sprint.deokhugam.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
  NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "사용자 정보가 없습니다."),
  DUPLICATE_EMAIL(HttpStatus.CONFLICT, "USER_DUPLICATE_EMAIL", "이미 사용 중인 이메일입니다."),
  LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "USER_LOGIN_FAILED", "이메일 또는 비밀번호가 일치하지 않습니다."),
  FORBIDDEN(HttpStatus.FORBIDDEN, "USER_FORBIDDEN", "본인 정보만 수정·삭제할 수 있습니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;
}
