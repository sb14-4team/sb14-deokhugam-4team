package com.sprint.deokhugam.dashboard.book.application;

import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.common.exception.CommonErrorCode;
import com.sprint.deokhugam.dashboard.book.domain.RankingPeriod;
import java.time.Instant;
import org.springframework.data.domain.Sort;

public record PopularBookSearchCondition(
    RankingPeriod period, Sort.Direction direction, Long cursor, Instant after, int limit) {

  public static PopularBookSearchCondition toCondition(
      RankingPeriod period, Sort.Direction direction, String cursor, Instant after, int limit) {
    if (period == null || direction == null) {
      throw new BusinessException(CommonErrorCode.INVALID_INPUT, "period와 direction은 함께 보내야 합니다.");
    }

    if ((cursor == null) != (after == null)) {
      throw new BusinessException(CommonErrorCode.INVALID_INPUT, "cursor와 after는 함께 보내야 합니다.");
    }

    Long cursorRank;
    try {
      cursorRank = cursor == null ? null : Long.parseLong(cursor);
    } catch (NumberFormatException e) {
      throw new BusinessException(
          CommonErrorCode.INVALID_INPUT, "인기 도서의 커서는 숫자여야 합니다. 값 > " + cursor);
    }
    if (cursorRank != null && cursorRank < 1) {
      throw new BusinessException(CommonErrorCode.INVALID_INPUT, "인기 도서의 커서는 1 이상의 값이여야 합니다.");
    }

    return new PopularBookSearchCondition(period, direction, cursorRank, after, limit);
  }

  public boolean isFirstPage() {
    return cursor == null;
  }

  public boolean isDescending() {
    return direction == Sort.Direction.DESC;
  }
}
