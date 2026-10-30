package com.sprint.deokhugam.review.application.required;

import com.sprint.deokhugam.review.application.dto.req.ReviewSearchCondition;
import com.sprint.deokhugam.review.domain.Review;
import java.util.List;
import java.util.UUID;

/*
 CQRS 조회용과 변경용을 구분하는 이유 알아보기
*/
public interface ReviewQueryPort {
  Review getByIdOrThrow(UUID reviewId);

  List<Review> search(ReviewSearchCondition condition);

  long count(ReviewSearchCondition condition);
}
