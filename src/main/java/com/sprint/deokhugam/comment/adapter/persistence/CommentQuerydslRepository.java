package com.sprint.deokhugam.comment.adapter.persistence;

import static com.sprint.deokhugam.comment.domain.QComment.comment;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentQuerydslRepository {

  private final JPAQueryFactory queryFactory;

  public void deleteCommentByUsers(List<UUID> userIds) {
    queryFactory.delete(comment).where(comment.user.id.in(userIds)).execute();
  }
}
