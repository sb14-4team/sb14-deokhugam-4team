package com.sprint.deokhugam.review.adapter.persistence;

import com.sprint.deokhugam.review.domain.ReviewLike;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewLikeJpaRepository extends JpaRepository<ReviewLike, UUID> {
  boolean existsByReview_IdAndUser_IdAndLikedTrue(UUID reviewId, UUID userId);
}
