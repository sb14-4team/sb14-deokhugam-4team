package com.sprint.deokhugam.review.adapter.persistence;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.deokhugam.review.application.dto.req.ReviewSearchCondition;
import com.sprint.deokhugam.review.domain.QReview;
import com.sprint.deokhugam.review.domain.Review;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ReviewQuerydslRepository {
  private final JPAQueryFactory queryFactory;

  public List<Review> search(ReviewSearchCondition condition) {
    QReview review = QReview.review;

    return null;
  }
}
