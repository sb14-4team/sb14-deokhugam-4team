package com.sprint.deokhugam.book.application.dto;

import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.common.exception.CommonErrorCode;
import java.time.Instant;
import org.springframework.data.domain.Sort;

// 도서 목록 검색 조건. 컨트롤러가 요청 파라미터로 만들어 서비스·저장소에 넘긴다
public record BookSearchCondition(
    String keyword, // 제목·저자·ISBN 부분 일치. 없으면 전체
    BookSortField sortField,
    Sort.Direction direction,
    String cursor, // 이전 페이지 마지막 도서의 정렬 기준 값. 첫 페이지면 null
    Instant after // 이전 페이지 마지막 도서의 createdAt. 동률일 때 2차 기준
    ) {

  public BookSearchCondition {
    // 공백만 있는 키워드는 없는 것으로 본다
    keyword = (keyword == null || keyword.isBlank()) ? null : keyword.trim();

    // cursor와 after는 한 쌍이다. 하나만 오면 어디부터 이어 줄지 정할 수 없다
    if ((cursor == null) != (after == null)) {
      throw new BusinessException(CommonErrorCode.INVALID_INPUT, "cursor와 after는 함께 보내야 합니다.");
    }

    // 커서가 정렬 기준 타입으로 읽히는지 여기서 미리 확인한다 (틀리면 400). 변환된 값은 저장소가 다시 만든다
    if (cursor != null) {
      sortField.parseCursor(cursor);
    }
  }

  public boolean isFirstPage() {
    return cursor == null;
  }

  public boolean isDescending() {
    return direction == Sort.Direction.DESC;
  }
}
