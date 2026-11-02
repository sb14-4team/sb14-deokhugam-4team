package com.sprint.deokhugam.review.application.provided.query;

import com.sprint.deokhugam.review.domain.Review;
import java.util.UUID;

/*
 논리 삭제된 리뷰는 제외하여 제공
*/
public interface ReviewFinder {
  // 리뷰 객체가 필요시
  Review getByIdOrThrow(UUID reviewId); // 조회 후 존재하지 않으면 not found 예외처리 - 호출지에선 id 로 조회 요청할것

  // 존재 검증만 필요하다면 - 논리삭제 되어있지 않아야함
  void requireActiveById(UUID reviewId);
}
