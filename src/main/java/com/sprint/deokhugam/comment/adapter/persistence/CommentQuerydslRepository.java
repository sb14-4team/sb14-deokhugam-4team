package com.sprint.deokhugam.comment.adapter.persistence;

import static com.sprint.deokhugam.comment.domain.QComment.comment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.deokhugam.comment.domain.Comment;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentQuerydslRepository {

  private final JPAQueryFactory queryFactory;
  private final EntityManager entityManager;

  // review 내 ID 조회로 발생한 N+1 문제를 해결하기 위해 조인.
  public List<Comment> findByUser_Id(UUID userId) {
    return queryFactory
        .selectFrom(comment)
        .join(comment.review)
        .fetchJoin()
        .where(comment.user.id.eq(userId))
        .fetch();
  }

  public void deleteCommentsByUser(UUID userId) {
    queryFactory.delete(comment).where(comment.user.id.eq(userId)).execute();
    entityManager.flush();
    entityManager.clear();
  }

  public void deleteCommentsByReview(List<UUID> reviewIds) {
    queryFactory.delete(comment).where(comment.review.id.in(reviewIds)).execute();
    entityManager.flush();
    entityManager.clear();
  }
}
