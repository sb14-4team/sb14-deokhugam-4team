package com.sprint.deokhugam.dashboard.book.application.provided.query;

import com.sprint.deokhugam.common.pagination.CursorPageResponse;
import com.sprint.deokhugam.dashboard.book.application.res.PopularBookResponse;

public interface PopularBookQuery {
  CursorPageResponse<PopularBookResponse> searchPopularBooksByPagination(
      PopularBookSearchCondition condition);
}
