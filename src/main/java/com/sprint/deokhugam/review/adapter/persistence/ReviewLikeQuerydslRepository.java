package com.sprint.deokhugam.review.adapter.persistence;

import static com.sprint.deokhugam.review.domain.QReviewLike.reviewLike;

import com.querydsl.jpa.impl.JPAQueryFactory;
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
}
