package com.sprint.deokhugam.review.adapter.persistence;

import static com.sprint.deokhugam.review.domain.QReviewLike.reviewLike;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ReviewLikeQuerydslRepository {
  private final JPAQueryFactory queryFactory;

  public void deleteAllByReviewId(UUID reviewId) {
    queryFactory.delete(reviewLike).where(reviewLike.review.id.eq(reviewId)).execute();
  }

  public long deleteAllByUserIds(List<UUID> userIds) {
    return queryFactory.delete(reviewLike).where(reviewLike.user.id.in(userIds)).execute();
  }

  public void deleteAllByReviewIds(List<UUID> reviewIds) {
    queryFactory.delete(reviewLike).where(reviewLike.review.id.in(reviewIds)).execute();
  }
}
