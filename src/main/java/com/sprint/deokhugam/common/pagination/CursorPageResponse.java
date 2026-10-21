package com.sprint.deokhugam.common.pagination;

import java.time.Instant;
import java.util.List;
import java.util.function.Function;

// 모든 목록 API의 응답
public record CursorPageResponse<T>(
    List<T> content,
    String nextCursor, // 숫자 정렬이어도 문자열
    Instant nextAfter,
    int size,
    long totalElements,
    boolean hasNext) {

  // rows는 limit + 1개까지 조회한 결과. 넘치면 다음 페이지가 있다
  public static <T> CursorPageResponse<T> of(
      List<T> rows,
      int limit,
      Function<T, Object> cursorOf,
      Function<T, Instant> afterOf,
      long totalElements) {
    boolean hasNext = rows.size() > limit;
    List<T> content = hasNext ? rows.subList(0, limit) : rows;

    if (!hasNext) {
      return new CursorPageResponse<>(content, null, null, content.size(), totalElements, false);
    }

    T last = content.getLast();
    return new CursorPageResponse<>(
        content,
        String.valueOf(cursorOf.apply(last)),
        afterOf.apply(last),
        content.size(),
        totalElements,
        true);
  }
}
