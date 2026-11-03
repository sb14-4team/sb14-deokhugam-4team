package com.sprint.deokhugam.dashboard.book.adapter.persistence;

import static com.sprint.deokhugam.dashboard.book.domain.QPopularBook.popularBook;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.deokhugam.dashboard.book.application.PopularBookSearchCondition;
import com.sprint.deokhugam.dashboard.book.domain.PopularBook;
import com.sprint.deokhugam.dashboard.book.domain.RankingPeriod;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PopularBookQuerydslRepository {
  private final JPAQueryFactory queryFactory;

  public List<PopularBook> searchByCondition(PopularBookSearchCondition condition) {
    LocalDate latestDate = findLatestBaseDate(condition.period()); // 리뷰 없어도 최신거 보여주기로함
    if (latestDate == null) return List.of();

    return queryFactory
        .selectFrom(popularBook)
        .join(popularBook.book)
        .fetchJoin()
        .where(
            popularBook.baseDate.eq(latestDate),
            periodCondition(condition),
            cursorCondition(condition))
        .orderBy(condition.isDescending() ? popularBook.rank.desc() : popularBook.rank.asc())
        .limit(condition.limit() + 1L)
        .fetch();
  }

  private LocalDate findLatestBaseDate(RankingPeriod period) {
    return queryFactory
        .select(popularBook.baseDate.max())
        .from(popularBook)
        .where(popularBook.period.eq(period))
        .fetchOne();
  }

  private BooleanExpression periodCondition(PopularBookSearchCondition condition) {
    return popularBook.period.eq(condition.period());
  }

  // after 안함
  private BooleanExpression cursorCondition(PopularBookSearchCondition condition) {
    if (condition.isFirstPage()) return null;

    return condition.isDescending()
        ? popularBook.rank.lt(condition.cursor())
        : popularBook.rank.gt(condition.cursor());
  }
}
