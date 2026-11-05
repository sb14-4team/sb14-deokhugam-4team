package com.sprint.deokhugam.dashboard.review.application.provided;

import com.sprint.deokhugam.common.pagination.CursorPageResponse;
import com.sprint.deokhugam.dashboard.review.application.dto.PopularReviewDto;
import com.sprint.deokhugam.dashboard.review.application.dto.PopularReviewSearchCondition;
import java.time.LocalDate;

public interface PopularReviewUseCase {

  CursorPageResponse<PopularReviewDto> searchPopularReviewsByPagination(
      PopularReviewSearchCondition condition);

  // 스케쥴러에서 0시마다 순위를 다시 계산해 저장
  void aggregatePopularReviews(LocalDate baseDate);
}
