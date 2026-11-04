package com.sprint.deokhugam.notification.adapter.persistence;

import static com.sprint.deokhugam.notification.domain.QNotification.notification;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.deokhugam.notification.application.dto.NotificationSearchCond;
import com.sprint.deokhugam.notification.domain.Notification;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationQRepository {

  private final JPAQueryFactory jpaQueryFactory;

  public List<UUID> findCleanupIds(Instant cutoff, int limit) {
    return jpaQueryFactory
        .select(notification.id)
        .from(notification)
        .where(notification.deletedAt.isNotNull().and(notification.deletedAt.loe(cutoff)))
        .orderBy(notification.deletedAt.asc(), notification.id.asc())
        .limit(limit)
        .fetch();
  }

  public long deleteConfirmed(Instant cutoff) {
    return jpaQueryFactory
        .delete(notification)
        .where(notification.confirmed.isTrue().and(notification.confirmedAt.loe(cutoff)))
        .execute();
  }

  public List<Notification> findAll(NotificationSearchCond cond) {
    boolean descending = cond.direction() == Direction.DESC;

    return jpaQueryFactory
        .selectFrom(notification)
        .where(
            notification
                .user
                .id
                .eq(cond.userId())
                .and(notification.deletedAt.isNull())
                .and(cursorCondition(cond)))
        .orderBy(
            descending ? notification.createdAt.desc() : notification.createdAt.asc(),
            descending ? notification.id.desc() : notification.id.asc())
        .limit(cond.limit() + 1L)
        .fetch();
  }

  public void deleteByUser(UUID userId) {
    jpaQueryFactory.delete(notification).where(notification.user.id.eq(userId)).execute();
  }

  public void deleteByReview(UUID reviewId) {
    jpaQueryFactory.delete(notification).where(notification.review.id.eq(reviewId)).execute();
  }

  public void deleteByReviewIds(List<UUID> reviewIds) {
    jpaQueryFactory.delete(notification).where(notification.review.id.in(reviewIds)).execute();
  }

  public long countByUser(UUID userId) {
    Long count =
        jpaQueryFactory
            .select(notification.count())
            .from(notification)
            .where(notification.user.id.eq(userId).and(notification.deletedAt.isNull()))
            .fetchOne();

    return count == null ? 0L : count;
  }

  private BooleanExpression cursorCondition(NotificationSearchCond cond) {
    if (cond.cursor() == null) {
      return null;
    }

    if (cond.direction().equals(Direction.DESC)) {
      return notification
          .createdAt
          .lt(cond.createdAt())
          .or(notification.createdAt.eq(cond.createdAt()).and(notification.id.lt(cond.cursor())));
    }
    return notification
        .createdAt
        .gt(cond.createdAt())
        .or(notification.createdAt.eq(cond.createdAt()).and(notification.id.gt(cond.cursor())));
  }
}
