package com.sprint.deokhugam.common.exception;

import lombok.Getter;

// 서비스는 이 예외만 던진다
@Getter
public class BusinessException extends RuntimeException {

  private final ErrorCode errorCode;

  public BusinessException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
