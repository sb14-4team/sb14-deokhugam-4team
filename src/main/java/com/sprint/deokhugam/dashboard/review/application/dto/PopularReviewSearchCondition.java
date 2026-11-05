package com.sprint.deokhugam.dashboard.review.application.dto;

import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.common.exception.CommonErrorCode;
import com.sprint.deokhugam.dashboard.review.domain.PopularReviewPeriod;
import java.time.Instant;
import org.springframework.data.domain.Sort;

public record PopularReviewSearchCondition(
    PopularReviewPeriod period, Sort.Direction direction, Long cursor, Instant after, int limit) {

  public static PopularReviewSearchCondition toCondition(
      PopularReviewPeriod period,
      Sort.Direction direction,
      String cursor,
      Instant after,
      Integer limit) {
    if (period == null || direction == null || limit == null) {
      throw new BusinessException(
          CommonErrorCode.INVALID_INPUT, "period, direction, limit는 필수입니다.");
    }

    if ((cursor == null) != (after == null)) {
      throw new BusinessException(CommonErrorCode.INVALID_INPUT, "cursor와 after는 함께 보내야 합니다.");
    }

    Long cursorRank;
    try {
      cursorRank = cursor == null ? null : Long.parseLong(cursor);
    } catch (NumberFormatException e) {
      throw new BusinessException(
          CommonErrorCode.INVALID_INPUT, "인기 리뷰의 커서는 순위(숫자)여야 합니다. 값 > " + cursor);
    }

    return new PopularReviewSearchCondition(period, direction, cursorRank, after, limit);
  }

  public boolean isFirstPage() {
    return cursor == null;
  }

  public boolean isDescending() {
    return direction == Sort.Direction.DESC;
  }
}
