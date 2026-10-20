package com.sprint.deokhugam.review.adapter.persistence;

import com.sprint.deokhugam.review.domain.Review;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewJpaRepository extends JpaRepository<Review, UUID> {
  // jpql

  // 포트가 spring data jpa 에 의존하게 되니까 유연성이 떨어진다. 다른 구현체에서 다른 필드를 리턴하고 싶을 때 문제가 발생한다
}
