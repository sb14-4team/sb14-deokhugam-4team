package com.sprint.deokhugam.common.exception;

import java.util.Map;

// details는 검증 실패 때만 채움
public record ErrorResponse(String code, String message, Map<String, String> details) {}
