package com.sprint.deokhugam.user.adapter.persistence;

import static com.sprint.deokhugam.user.domain.QUser.user;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class UserQuerydslRepository {

  private final JPAQueryFactory jpaQueryFactory;

  List<UUID> findCleanupIds(Instant cutoff, int limit) {
    return jpaQueryFactory
        .select(user.id)
        .from(user)
        .where(user.deletedAt.isNotNull().and(user.deletedAt.loe(cutoff)))
        .orderBy(user.deletedAt.asc(), user.id.asc())
        .limit(limit)
        .fetch();
  }
}
