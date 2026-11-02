package com.sprint.deokhugam.dashboard.review.application.required;

import com.sprint.deokhugam.dashboard.review.application.dto.PopularReviewDto;
import com.sprint.deokhugam.dashboard.review.application.dto.PopularReviewSearchCondition;
import com.sprint.deokhugam.dashboard.review.application.dto.ReviewActivity;
import com.sprint.deokhugam.dashboard.review.domain.PopularReviewPeriod;
import java.util.List;

public interface PopularReviewSearchPort {

  // limit + 1개까지 조회
  List<PopularReviewDto> search(PopularReviewSearchCondition condition);

  // PopularReview 개수
  long count(PopularReviewSearchCondition condition);

  // 기간 안에 좋아요나 댓글이 한 번이라도 생긴 리뷰들의 활동 수
  List<ReviewActivity> findReviewActivities(PopularReviewPeriod.Range range);
}
