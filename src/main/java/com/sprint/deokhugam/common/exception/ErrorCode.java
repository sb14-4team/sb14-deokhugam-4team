package com.sprint.deokhugam.common.exception;

import org.springframework.http.HttpStatus;

// 도메인마다 enum으로 구현 (UserErrorCode …)
public interface ErrorCode {

  HttpStatus getStatus();

  String getCode(); // 예: USER_NOT_FOUND

  String getMessage();
}
