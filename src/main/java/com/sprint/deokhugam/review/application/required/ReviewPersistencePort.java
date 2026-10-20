package com.sprint.deokhugam.review.application.required;

import com.sprint.deokhugam.review.domain.Review;

public interface ReviewPersistencePort {
  // save
  Review save(Review review);

  // 필요메서드를 정정 다른 데이터소스의 반환값이 다를 수 있으니까
}
