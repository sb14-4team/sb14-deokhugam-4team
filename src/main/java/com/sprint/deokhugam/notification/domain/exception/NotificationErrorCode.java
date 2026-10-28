package com.sprint.deokhugam.notification.domain.exception;

import com.sprint.deokhugam.common.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NotificationErrorCode implements ErrorCode {
  NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION_NOT_FOUND", "알림 정보가 없습니다."),
  FORBIDDEN(HttpStatus.FORBIDDEN, "NOTIFICATION_FORBIDDEN", "본인의 알림만 수정할 수 있습니다.");

  private final HttpStatus status;
  private final String code;
  private final String message;
}
