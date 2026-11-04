package com.sprint.deokhugam.dashboard.book.application.provided.query;

import com.sprint.deokhugam.common.pagination.CursorPageResponse;
import com.sprint.deokhugam.dashboard.book.application.required.PopularBookQueryPort;
import com.sprint.deokhugam.dashboard.book.application.res.PopularBookResponse;
import com.sprint.deokhugam.dashboard.book.domain.PopularBook;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PopularBookQueryService implements PopularBookQuery {
  private final PopularBookQueryPort popularBookQueryPort;

  @Override
  @Transactional
  public CursorPageResponse<PopularBookResponse> searchPopularBooksByPagination(
      PopularBookSearchCondition condition) {
    List<PopularBook> popularBookList = popularBookQueryPort.search(condition);
    List<PopularBookResponse> responses =
        popularBookList.stream().map(PopularBookResponse::from).toList();

    return toCursorPage(responses, condition, responses.size()); // 걍 사이즈 던짐
  }

  private CursorPageResponse<PopularBookResponse> toCursorPage(
      List<PopularBookResponse> responses,
      PopularBookSearchCondition condition,
      long totalElements) {

    return CursorPageResponse.of(
        responses,
        condition.limit(),
        PopularBookResponse::rank,
        PopularBookResponse::createdAt,
        totalElements);
  }
}
