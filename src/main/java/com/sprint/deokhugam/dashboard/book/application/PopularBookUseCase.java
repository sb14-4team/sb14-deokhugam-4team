package com.sprint.deokhugam.dashboard.book.application;

import com.sprint.deokhugam.common.pagination.CursorPageResponse;
import com.sprint.deokhugam.dashboard.book.application.res.PopularBookResponse;

public interface PopularBookUseCase {
  CursorPageResponse<PopularBookResponse> searchPopularBooks(PopularBookSearchCondition condition);
}
