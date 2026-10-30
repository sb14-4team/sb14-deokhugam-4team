package com.sprint.deokhugam.review.adapter.persistence;

import com.sprint.deokhugam.review.application.required.ReviewLikeQueryPort;
import com.sprint.deokhugam.review.application.required.ReviewLikeRepositoryPort;
import com.sprint.deokhugam.review.domain.ReviewLike;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReviewLikeRepositoryAdapter implements ReviewLikeQueryPort, ReviewLikeRepositoryPort {
  private final ReviewLikeJpaRepository reviewLikeJpaRepository;
  private final ReviewLikeQuerydslRepository reviewLikeQuerydslRepository;

  @Override
  public ReviewLike save(ReviewLike reviewLike) {
    return reviewLikeJpaRepository.save(reviewLike);
  }

  @Override
  public boolean isLikedBy(UUID reviewId, UUID userId) {
    return reviewLikeJpaRepository.existsByReview_IdAndUser_IdAndLikedTrue(reviewId, userId);
  }

  @Override
  public List<UUID> findReviewLikeIdsByRequestId(List<UUID> reviewIds, UUID requestUserId) {
    return reviewLikeJpaRepository.findReviewLikeIds(requestUserId, reviewIds);
  }

  @Override
  public Optional<ReviewLike> getByUserIdAndReviewId(UUID userId, UUID reviewId) {
    return reviewLikeJpaRepository.findByUser_IdAndReview_Id(userId, reviewId);
  }

  @Override
  public void deleteAllByReviewId(UUID reviewId) {
    reviewLikeQuerydslRepository.deleteAllByReviewId(reviewId);
  }

  @Override
  public long deleteAllByUserIds(List<UUID> userIds) {
    return reviewLikeQuerydslRepository.deleteAllByUserIds(userIds);
  }

  @Override
  public void deleteAllByReviewIds(List<UUID> reviewIds) {
    reviewLikeQuerydslRepository.deleteAllByReviewIds(reviewIds);
  }

  @Override
  public long deleteAllByUserIdsOrReviewIds(List<UUID> userIds, List<UUID> reviewIds) {
    return reviewLikeQuerydslRepository.deleteAllByUserIdsOrReviewIds(userIds, reviewIds);
  }
}
