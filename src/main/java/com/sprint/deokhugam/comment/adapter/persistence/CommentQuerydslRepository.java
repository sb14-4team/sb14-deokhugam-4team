package com.sprint.deokhugam.comment.adapter.persistence;

import static com.sprint.deokhugam.comment.domain.QComment.comment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentQuerydslRepository {

  private final JPAQueryFactory queryFactory;

  public void deleteCommentsByUser(UUID userId) {
    queryFactory.delete(comment).where(comment.user.id.eq(userId)).execute();
  }

  public void deleteCommentsByReview(UUID reviewId) {
    queryFactory.delete(comment).where(comment.review.id.eq(reviewId)).execute();
  }
}
