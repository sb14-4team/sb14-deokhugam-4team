package com.sprint.deokhugam.review.adapter.persistence;

import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.domain.review.exception.ReviewErrorCode;
import com.sprint.deokhugam.review.domain.Review;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

// 포트가 spring data jpa 에 의존하게 되니까 유연성이 떨어진다. 다른 구현체에서 다른 필드를 리턴하고 싶을 때 문제가 발생한다 - 안봐도됨
public interface ReviewJpaRepository extends JpaRepository<Review, UUID> {
  boolean existsByIdAndDeletedAtIsNull(UUID reviewId);

  Optional<Review> findByIdAndDeletedAtIsNull(UUID reviewId);

  @Query(
      """
    select case when count(r) > 0 then true else false end
    from Review r
    where r.user.id = :userId
      and r.book.id = :bookId
      and r.deletedAt is null
    """)
  boolean existsActiveReview(UUID userId, UUID bookId);

  default Review getByIdOrThrow(UUID reviewId) {
    return findByIdAndDeletedAtIsNull(reviewId)
        .orElseThrow(() -> new BusinessException(ReviewErrorCode.NOT_FOUND));
  }
}
