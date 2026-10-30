package com.sprint.deokhugam.review.adapter.persistence;

import com.sprint.deokhugam.review.application.dto.req.ReviewSearchCondition;
import com.sprint.deokhugam.review.application.required.ReviewQueryPort;
import com.sprint.deokhugam.review.application.required.ReviewRepositoryPort;
import com.sprint.deokhugam.review.domain.Review;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/*
 구현체 분리 고려해보기
 - 포트를 막상 나눴지만 구현체는 합쳐져있음. 현재 리뷰/좋아요 함께 묶여있어서 클래스파일 너무 많아짐. 이대로 진행
*/
// r2dbc redis
@RequiredArgsConstructor
@Repository
public class ReviewRepositoryAdapter implements ReviewQueryPort, ReviewRepositoryPort {
  private final ReviewJpaRepository reviewJpaRepository;
  private final ReviewQuerydslRepository reviewQuerydslRepository;

  @Override
  public Review save(Review review) {
    return reviewJpaRepository.save(review);
  }

  @Override
  public Review getByIdOrThrow(UUID reviewId) {
    return reviewJpaRepository.getByIdOrThrow(reviewId);
  }

  @Override
  public Optional<Review> getByIdIncludingDeleted(UUID reviewId) {
    return reviewJpaRepository.findById(reviewId);
  }

  @Override
  public List<Review> getByIdsIncludingDeleted(List<UUID> reviewIds) {
    return reviewQuerydslRepository.getByIdsIncludingDeleted(reviewIds);
  }

  @Override
  public void requireActiveById(UUID reviewId) {
    reviewJpaRepository.requireActiveById(reviewId);
  }

  @Override
  public boolean existsByUserIdAndBookId(UUID userId, UUID bookId) {
    return reviewJpaRepository.existsActiveReview(userId, bookId);
  }

  @Override
  public void delete(Review review) {
    reviewJpaRepository.delete(review);
  }

  @Override
  public List<Review> search(ReviewSearchCondition condition) {
    return reviewQuerydslRepository.search(condition);
  }

  @Override
  public long count(ReviewSearchCondition condition) {
    return reviewQuerydslRepository.count(condition);
  }

  @Override
  public long deleteAllByIds(List<UUID> reviewIds) {
    return reviewQuerydslRepository.deleteAllByIds(reviewIds);
  }

  @Override
  public void decreaseLikeCountsByUserIds(List<UUID> userIds) {
    reviewJpaRepository.decreaseLikeCountsByUserIds(userIds);
  }

  @Override
  public List<UUID> getIdsByUserIds(List<UUID> userIds) {
    return reviewQuerydslRepository.getIdsByUserIds(userIds);
  }

  @Override
  public List<UUID> getIdsByBookId(UUID bookId) {
    return reviewQuerydslRepository.getIdsByBookId(bookId);
  }
}
