package com.sprint.deokhugam.dashboard.review.application.required;

import com.sprint.deokhugam.dashboard.review.domain.PopularReview;
import com.sprint.deokhugam.dashboard.review.domain.PopularReviewPeriod;
import java.util.List;

public interface PopularReviewPersistencePort {

  // 해당 기간의 기존 순위를 지우고 새 순위로 바꾼다
  void replaceAll(PopularReviewPeriod period, List<PopularReview> popularReviews);
}
