package com.sprint.deokhugam.common.exception;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 그 밖의 예외는 스프링 기본 처리에 맡긴다 (전체를 잡으면 400·404도 500이 됨)
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusiness(BusinessException e) {
    ErrorCode errorCode = e.getErrorCode();
    log.info("요청 실패: {}", errorCode.getCode());
    return ResponseEntity.status(errorCode.getStatus())
        .body(new ErrorResponse(errorCode.getCode(), errorCode.getMessage(), Map.of()));
  }

  // @Valid @RequestBody 검증 실패
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
    Map<String, String> details = new HashMap<>();
    for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
      details.put(fieldError.getField(), fieldError.getDefaultMessage());
    }
    ErrorCode errorCode = CommonErrorCode.INVALID_INPUT;
    return ResponseEntity.status(errorCode.getStatus())
        .body(new ErrorResponse(errorCode.getCode(), errorCode.getMessage(), details));
  }
}
