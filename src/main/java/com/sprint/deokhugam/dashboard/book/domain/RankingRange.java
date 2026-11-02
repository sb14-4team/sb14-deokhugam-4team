package com.sprint.deokhugam.dashboard.book.domain;

import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.common.exception.CommonErrorCode;
import java.time.Instant;

public sealed interface RankingRange permits RankingRange.Between, RankingRange.AllTime {

  record Between(Instant startAt, Instant endAt) implements RankingRange {
    public Between {
      if (startAt == null || endAt == null || !startAt.isBefore(endAt)) {
        throw new BusinessException(CommonErrorCode.INVALID_INPUT);
      }
    }
  }

  record AllTime() implements RankingRange {}
}
