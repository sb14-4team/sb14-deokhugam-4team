package com.sprint.deokhugam.user.application.dto;

import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.common.exception.CommonErrorCode;
import com.sprint.deokhugam.user.domain.ranking.RankingPeriod;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.UUID;

public record PowerUserCursor(
    RankingPeriod period, LocalDate publishedOn, long scoreUnits, UUID userId) {

  public PowerUserCursor {
    if (period == null || publishedOn == null || userId == null || scoreUnits < 0) {
      throw new BusinessException(CommonErrorCode.INVALID_INPUT);
    }
  }

  public String encode() {
    return period + "|" + publishedOn + "|" + scoreUnits + "|" + userId;
  }

  public static PowerUserCursor decode(String value) {
    if (value == null) {
      return null;
    }

    String[] parts = value.split("\\|", -1);

    if (parts.length != 4) {
      throw new BusinessException(CommonErrorCode.INVALID_INPUT);
    }

    try {
      return new PowerUserCursor(
          RankingPeriod.valueOf(parts[0]),
          LocalDate.parse(parts[1]),
          Long.parseLong(parts[2]),
          UUID.fromString(parts[3]));
    } catch (IllegalArgumentException | DateTimeParseException e) {
      throw new BusinessException(CommonErrorCode.INVALID_INPUT, "파워 유저 커서 형식이 올바르지 않습니다.", e);
    }
  }
}
