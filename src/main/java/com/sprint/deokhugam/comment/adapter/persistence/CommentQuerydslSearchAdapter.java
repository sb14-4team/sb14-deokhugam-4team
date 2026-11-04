package com.sprint.deokhugam.comment.adapter.persistence;

import static com.sprint.deokhugam.comment.domain.QComment.comment;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.deokhugam.comment.application.dto.CommentSearchCondition;
import com.sprint.deokhugam.comment.application.required.CommentSearchPort;
import com.sprint.deokhugam.comment.domain.Comment;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class CommentQuerydslSearchAdapter implements CommentSearchPort {
  private final JPAQueryFactory queryFactory;

  @Override
  public List<Comment> search(CommentSearchCondition condition) {
    return queryFactory
        .selectFrom(comment)
        .where(
            comment.deletedAt.isNull(),
            reviewIdCondition(condition.reviewId()),
            cursorCondition(condition))
        .orderBy(orderSpecifiers(condition))
        .limit(condition.limit() + 1)
        .fetch();
  }

  @Override
  public long count(CommentSearchCondition condition) {
    Long count =
        queryFactory
            .select(comment.count())
            .from(comment)
            .where(comment.deletedAt.isNull(), reviewIdCondition(condition.reviewId()))
            .fetchOne();

    return count == null ? 0L : count;
  }

  private BooleanExpression reviewIdCondition(UUID reviewId) {
    return comment.review.id.eq(reviewId);
  }

  private OrderSpecifier<?>[] orderSpecifiers(CommentSearchCondition condition) {
    return new OrderSpecifier<?>[] {
      condition.isDescending() ? comment.createdAt.desc() : comment.createdAt.asc()
    };
  }

  private BooleanExpression cursorCondition(CommentSearchCondition condition) {
    if (condition.isFirstPage()) return null;

    return condition.isDescending()
        ? comment.createdAt.lt(condition.cursor())
        : comment.createdAt.gt(condition.cursor());
  }
}
