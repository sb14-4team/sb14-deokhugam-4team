package com.sprint.deokhugam.common.exception;

import java.util.Map;

// details는 @Valid 검증 실패(필드별 메시지)와 헤더 누락(header) 때만 채움
public record ErrorResponse(String code, String message, Map<String, String> details) {}
