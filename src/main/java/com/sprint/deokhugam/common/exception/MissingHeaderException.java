package com.sprint.deokhugam.common.exception;

import lombok.Getter;

@Getter
public class MissingHeaderException extends BusinessException {

  private final String headerName;

  public MissingHeaderException(String headerName) {
    super(CommonErrorCode.MISSING_HEADER);
    this.headerName = headerName;
  }
}
