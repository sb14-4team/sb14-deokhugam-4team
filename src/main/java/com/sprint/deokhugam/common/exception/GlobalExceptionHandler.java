package com.sprint.deokhugam.common.exception;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

// 모든 오류를 ErrorResponse 형식으로 응답한다
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  // 도메인 에러 핸들러
  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusiness(BusinessException e) {
    String code = e.getErrorCode().getCode();
    if (e.getCause() != null) {
      // 원인 예외가 있으면 스택까지 남겨 원인을 찾을 수 있게 한다
      log.warn("요청 실패: {} - {}", code, e.getMessage(), e);
    } else {
      log.info("요청 실패: {} - {}", code, e.getMessage());
    }
    return toResponse(e.getErrorCode(), Map.of());
  }

  // @Valid @RequestBody 검증 실패 (1)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
    Map<String, String> details = new HashMap<>();
    for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
      details.put(fieldError.getField(), fieldError.getDefaultMessage());
    }
    log.info("검증 실패: {}", details);
    return toResponse(CommonErrorCode.INVALID_INPUT, details);
  }

  // @RequestHeader 필수 헤더 누락
  @ExceptionHandler(MissingRequestHeaderException.class)
  public ResponseEntity<ErrorResponse> handleMissingHeader(MissingRequestHeaderException e) {
    log.info("헤더 없음: {}", e.getHeaderName());
    return toResponse(CommonErrorCode.MISSING_HEADER, Map.of("header", e.getHeaderName()));
  }

  // 파라미터 누락·형식 오류, JSON 깨짐, @Min 같은 파라미터 검증 실패
  @ExceptionHandler({
    MissingServletRequestParameterException.class,
    MissingServletRequestPartException.class,
    MethodArgumentTypeMismatchException.class,
    HttpMessageNotReadableException.class,
    HandlerMethodValidationException.class,
    HttpMediaTypeNotSupportedException.class
  })
  public ResponseEntity<ErrorResponse> handleBadRequest(Exception e) {
    log.info("잘못된 요청: {}", e.getMessage());
    return toResponse(CommonErrorCode.INVALID_INPUT, Map.of());
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<ErrorResponse> handleFileTooLarge(MaxUploadSizeExceededException e) {
    log.info("파일 크기 초과: {}", e.getMessage());
    return toResponse(CommonErrorCode.FILE_TOO_LARGE, Map.of());
  }

  @ExceptionHandler({NoResourceFoundException.class, NoHandlerFoundException.class})
  public ResponseEntity<ErrorResponse> handleNotFound(Exception e) {
    log.info("없는 주소: {}", e.getMessage());
    return toResponse(CommonErrorCode.NOT_FOUND, Map.of());
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public ResponseEntity<ErrorResponse> handleMethodNotAllowed(
      HttpRequestMethodNotSupportedException e) {
    log.info("지원하지 않는 메서드: {}", e.getMethod());
    return toResponse(CommonErrorCode.METHOD_NOT_ALLOWED, Map.of());
  }

  // 위에서 잡지 못한 모든 예외
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleUnknown(Exception e) {
    log.error("알 수 없는 오류", e);
    return toResponse(CommonErrorCode.INTERNAL_ERROR, Map.of());
  }

  private ResponseEntity<ErrorResponse> toResponse(
      ErrorCode errorCode, Map<String, String> details) {
    return ResponseEntity.status(errorCode.getStatus())
        .body(new ErrorResponse(errorCode.getCode(), errorCode.getMessage(), details));
  }
}
