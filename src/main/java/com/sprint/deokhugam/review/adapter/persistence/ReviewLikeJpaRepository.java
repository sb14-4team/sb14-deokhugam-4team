package com.sprint.deokhugam.review.adapter.persistence;

import com.sprint.deokhugam.review.domain.ReviewLike;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ReviewLikeJpaRepository extends JpaRepository<ReviewLike, UUID> {
  boolean existsByReview_IdAndUser_IdAndLikedTrue(UUID reviewId, UUID userId);

  Optional<ReviewLike> findByUser_IdAndReview_Id(UUID userId, UUID reviewId);

  @Query(
      """
      select distinct rl.review.id
      from ReviewLike rl
      where rl.user.id = :userId
        and rl.review.id in :reviewIds
        and rl.liked = true
      """)
  List<UUID> findReviewLikeIds(UUID userId, List<UUID> reviewIds);
}
