package com.sprint.deokhugam.review.application.provided.query;

import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.common.exception.CommonErrorCode;
import java.time.Instant;

public sealed interface ReviewAggregationRange
    permits ReviewAggregationRange.Between, ReviewAggregationRange.AllTime {

  static ReviewAggregationRange between(Instant startAt, Instant endAt) {
    return new Between(startAt, endAt);
  }

  static ReviewAggregationRange allTime() {
    return new AllTime();
  }

  record Between(Instant startAt, Instant endAt) implements ReviewAggregationRange {
    public Between {
      if (startAt == null || endAt == null || !startAt.isBefore(endAt)) {
        throw new BusinessException(CommonErrorCode.INVALID_INPUT);
      }
    }
  }

  record AllTime() implements ReviewAggregationRange {}
}
