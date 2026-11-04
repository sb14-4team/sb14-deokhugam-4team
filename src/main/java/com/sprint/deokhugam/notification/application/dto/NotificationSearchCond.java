package com.sprint.deokhugam.notification.application.dto;

import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.common.exception.CommonErrorCode;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.domain.Sort.Direction;

public record NotificationSearchCond(
    UUID userId, Direction direction, UUID cursor, Instant createdAt, int limit) {

  public NotificationSearchCond {
    if (userId == null) {
      throw new BusinessException(CommonErrorCode.INVALID_INPUT);
    }
    if ((cursor == null) != (createdAt == null)) {
      throw new BusinessException(CommonErrorCode.INVALID_INPUT);
    }
  }

  public static NotificationSearchCond to(
      UUID userId, String direction, String cursor, Instant createdAt, int limit) {
    UUID cursorId = cursor == null ? null : UUID.fromString(cursor);

    Direction sort = direction == null ? Direction.DESC : Direction.fromString(direction);

    return new NotificationSearchCond(userId, sort, cursorId, createdAt, limit);
  }
}
