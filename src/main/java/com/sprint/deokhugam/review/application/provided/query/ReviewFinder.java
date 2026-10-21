package com.sprint.deokhugam.review.application.provided.query;

import com.sprint.deokhugam.review.domain.Review;
import java.util.UUID;

public interface ReviewFinder {
  Review getByIdOrThrow(UUID reviewId); // 조회 후 존재하지 않으면 not found 예외처리 - 호출지에선 id 로 조회 요청할것
}
