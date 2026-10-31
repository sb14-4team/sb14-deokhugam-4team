package com.sprint.deokhugam.review.adapter.persistence;

import static com.sprint.deokhugam.review.domain.QReviewLike.reviewLike;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
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

  public long deleteAllByUserId(UUID userId) {
    return queryFactory.delete(reviewLike).where(reviewLike.user.id.eq(userId)).execute();
  }

  public void deleteAllByReviewIds(List<UUID> reviewIds) {
    queryFactory.delete(reviewLike).where(reviewLike.review.id.in(reviewIds)).execute();
  }

  /*
   DELETE FROM review_likes
   WHERE user_id IN (...)
      OR review_id IN (...);
  */
  public long deleteAllByUserIdsOrReviewIds(List<UUID> userIds, List<UUID> reviewIds) {
    return queryFactory
        .delete(reviewLike)
        .where(
            ExpressionUtils.anyOf(
                listCondition(reviewLike.user.id, userIds),
                listCondition(reviewLike.review.id, reviewIds)))
        .execute();
  }

  private BooleanExpression listCondition(SimpleExpression<UUID> path, List<UUID> listIds) {
    if (listIds.isEmpty()) return null;

    return path.in(listIds);
  }
}
