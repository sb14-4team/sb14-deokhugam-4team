package com.sprint.deokhugam.common.exception;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BusinessExceptionTest {

  private final ErrorCode errorCode = CommonErrorCode.INVALID_INPUT;

  @Test
  @DisplayName("에러 코드만 넘기면 메시지는 에러 코드의 메시지다")
  void onlyErrorCode() {
    BusinessException e = new BusinessException(errorCode);

    assertThat(e.getErrorCode()).isEqualTo(errorCode);
    assertThat(e.getMessage()).isEqualTo("요청 값이 올바르지 않습니다.");
    assertThat(e.getCause()).isNull();
  }

  @Test
  @DisplayName("로그 메시지를 넘기면 예외 메시지가 그 값이 된다")
  void withLogMessage() {
    BusinessException e = new BusinessException(errorCode, "cursor=abc");

    assertThat(e.getMessage()).isEqualTo("cursor=abc");
    assertThat(e.getCause()).isNull();
  }

  @Test
  @DisplayName("원인 예외를 넘기면 getCause()로 꺼낼 수 있다")
  void withCause() {
    IOException cause = new IOException("파일 읽기 실패");

    BusinessException e = new BusinessException(errorCode, cause);

    assertThat(e.getCause()).isSameAs(cause);
    assertThat(e.getMessage()).isEqualTo("요청 값이 올바르지 않습니다.");
  }

  @Test
  @DisplayName("로그 메시지와 원인 예외를 함께 넘길 수 있다")
  void withLogMessageAndCause() {
    IOException cause = new IOException("파일 읽기 실패");

    BusinessException e = new BusinessException(errorCode, "isbn=123", cause);

    assertThat(e.getMessage()).isEqualTo("isbn=123");
    assertThat(e.getCause()).isSameAs(cause);
    assertThat(e.getErrorCode()).isEqualTo(errorCode);
  }
}
