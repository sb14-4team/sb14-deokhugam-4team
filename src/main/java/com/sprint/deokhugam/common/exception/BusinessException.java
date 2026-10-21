package com.sprint.deokhugam.common.exception;

import lombok.Getter;

// 서비스는 이 예외만 던진다
// 응답 메시지는 항상 errorCode의 메시지. logMessage와 cause는 로그에만 남는다 (사용자에게 안 보임)
@Getter
public class BusinessException extends RuntimeException {

  private final ErrorCode errorCode;

  // 예: throw new BusinessException(BookErrorCode.NOT_FOUND);
  public BusinessException(ErrorCode errorCode) {
    this(errorCode, errorCode.getMessage(), null);
  }

  // 로그에 남길 설명을 덧붙일 때
  // 예: throw new BusinessException(BookErrorCode.NOT_FOUND, "bookId=" + bookId);
  public BusinessException(ErrorCode errorCode, String logMessage) {
    this(errorCode, logMessage, null);
  }

  // 다른 예외를 잡아서 우리 오류로 바꿀 때. 원래 예외(cause)를 함께 넘겨야 로그에서 원인을 찾을 수 있다
  // 예: catch (IOException e) { throw new BusinessException(BookErrorCode.OCR_FAILED, e); }
  public BusinessException(ErrorCode errorCode, Throwable cause) {
    this(errorCode, errorCode.getMessage(), cause);
  }

  // 예: throw new BusinessException(BookErrorCode.INFO_NOT_FOUND, "isbn=" + isbn, e);
  public BusinessException(ErrorCode errorCode, String logMessage, Throwable cause) {
    super(logMessage, cause);
    this.errorCode = errorCode;
  }
}
