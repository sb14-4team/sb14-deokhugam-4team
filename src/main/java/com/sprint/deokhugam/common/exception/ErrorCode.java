package com.sprint.deokhugam.common.exception;

import org.springframework.http.HttpStatus;

// 도메인마다 enum으로 구현. 사용: throw new BusinessException(UserErrorCode.NOT_FOUND)
//
// 사용 규칙
// - 서비스는 자기 도메인 ErrorCode로 BusinessException만 던진다.
// - 요청 형식 오류(@Valid 등)는 개발자가 던질 일 없음
//   GlobalExceptionHandler가  CommonErrorCode로 알아서 바꾼다.
// - 어노테이션(@Valid 등)으로 못 거르는 형식 오류(허용되지 않은 정렬 값, 해석할 수 없는 커서 등)만
//   CommonErrorCode.INVALID_INPUT을 직접 던진다.
// - 도메인 의미가 있는 오류(중복, 권한 없음 등)는 도메인 ErrorCode에 추가한다.
//   CommonErrorCode에는 넣지 않는다.
// - INTERNAL_ERROR는 직접 던지지 않는다. 예상 못 한 예외는 catch하지 말고 그대로 둔다.
public interface ErrorCode {

  HttpStatus getStatus();

  String getCode(); // {도메인}_{항목 이름}. 예: USER_NOT_FOUND

  String getMessage();
}
