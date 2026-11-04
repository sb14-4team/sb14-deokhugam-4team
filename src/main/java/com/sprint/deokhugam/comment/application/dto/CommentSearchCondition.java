package com.sprint.deokhugam.comment.application.dto;

import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.common.exception.CommonErrorCode;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.UUID;
import org.springframework.data.domain.Sort;

public record CommentSearchCondition(
    UUID reviewId, Sort.Direction direction, Instant cursor, Instant after, Integer limit) {
  public static CommentSearchCondition toCondition(
      UUID reviewId, Sort.Direction direction, String cursor, Instant after, Integer limit) {
    if (reviewId == null || limit == null) {
      throw new BusinessException(CommonErrorCode.INVALID_INPUT, "리뷰아이디와 사이즈 수를 함께 보내야 합니다.");
    }

    if ((cursor == null) != (after == null)) {
      throw new BusinessException(CommonErrorCode.INVALID_INPUT, "cursor와 after는 함께 보내야 합니다.");
    }

    Instant cursorTime; // 댓글에서 정렬은 시간순 밖에 없어서 클래스 내부에서 인스턴트 타입으로 변환 (컨트롤러는 문자열 -> 인스턴트)
    try {
      cursorTime = cursor == null ? null : Instant.parse(cursor);
    } catch (DateTimeParseException e) {
      throw new BusinessException(
          CommonErrorCode.INVALID_INPUT, "댓글의 커서를 날짜 형식에 맞춰 보내주세요 값 > " + cursor);
    }

    return new CommentSearchCondition(reviewId, direction, cursorTime, after, limit);
  }

  public boolean isFirstPage() {
    return cursor == null;
  }

  public boolean isDescending() {
    return direction == Sort.Direction.DESC;
  }
}
