package com.sprint.deokhugam.book.adapter.persistence;

import static com.sprint.deokhugam.book.domain.QBook.book;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.ComparableExpressionBase;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.deokhugam.book.application.dto.BookSearchCondition;
import com.sprint.deokhugam.book.application.dto.BookSortField;
import com.sprint.deokhugam.book.domain.Book;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

// 도서 목록 검색 쿼리. 조건(키워드·커서·정렬)이 요청마다 달라서 Querydsl로 조립한다
@Repository
@RequiredArgsConstructor
public class BookSearchRepository {

  private final JPAQueryFactory queryFactory;

  public List<Book> search(BookSearchCondition condition, int size) {
    // where()에 null을 넘기면 그 조건은 무시된다 → 키워드·커서가 없을 때 조건이 빠진다
    return queryFactory
        .selectFrom(book)
        .where(notDeleted(), matchesKeyword(condition.keyword()), afterCursor(condition))
        .orderBy(orderBy(condition))
        .limit(size)
        .fetch();
  }

  public long countByKeyword(String keyword) {
    Long count =
        queryFactory
            .select(book.count())
            .from(book)
            .where(notDeleted(), matchesKeyword(keyword))
            .fetchOne();
    return count == null ? 0 : count;
  }

  // 논리 삭제 제외
  private BooleanExpression notDeleted() {
    return book.deletedAt.isNull();
  }

  // 제목·저자·ISBN 중 하나라도 키워드를 포함하면 통과 (LIKE '%키워드%', 대소문자 무시)
  private BooleanExpression matchesKeyword(String keyword) {
    if (keyword == null) {
      return null;
    }
    return book.title
        .containsIgnoreCase(keyword)
        .or(book.author.containsIgnoreCase(keyword))
        .or(book.isbn.contains(keyword));
  }

  // ORDER BY 정렬기준 방향, createdAt 방향 (같은 값이면 등록 시각으로 가른다)
  private OrderSpecifier<?>[] orderBy(BookSearchCondition condition) {
    ComparableExpressionBase<?> sortPath =
        switch (condition.sortField()) {
          case TITLE -> book.title;
          case PUBLISHED_DATE -> book.publishedDate;
          case RATING -> book.rating;
          case REVIEW_COUNT -> book.reviewCount;
        };
    if (condition.isDescending()) {
      return new OrderSpecifier<?>[] {sortPath.desc(), book.createdAt.desc()};
    }
    return new OrderSpecifier<?>[] {sortPath.asc(), book.createdAt.asc()};
  }

  // 커서 다음 행만 고른다. DESC면 "정렬값 < cursor OR (정렬값 = cursor AND createdAt < after)"
  // ASC면 부등호가 반대. 정렬 기준마다 컬럼 타입이 달라서 네 가지를 따로 쓴다
  private BooleanExpression afterCursor(BookSearchCondition condition) {
    if (condition.isFirstPage()) {
      return null;
    }
    boolean desc = condition.isDescending();
    Instant after = condition.after();
    BooleanExpression tieBreak = desc ? book.createdAt.lt(after) : book.createdAt.gt(after);

    BookSortField field = condition.sortField();
    Object cursor = field.parseCursor(condition.cursor());
    return switch (field) {
      case TITLE -> {
        String value = (String) cursor;
        yield (desc ? book.title.lt(value) : book.title.gt(value))
            .or(book.title.eq(value).and(tieBreak));
      }
      case PUBLISHED_DATE -> {
        LocalDate value = (LocalDate) cursor;
        yield (desc ? book.publishedDate.lt(value) : book.publishedDate.gt(value))
            .or(book.publishedDate.eq(value).and(tieBreak));
      }
      case RATING -> {
        Double value = (Double) cursor;
        yield (desc ? book.rating.lt(value) : book.rating.gt(value))
            .or(book.rating.eq(value).and(tieBreak));
      }
      case REVIEW_COUNT -> {
        Integer value = (Integer) cursor;
        yield (desc ? book.reviewCount.lt(value) : book.reviewCount.gt(value))
            .or(book.reviewCount.eq(value).and(tieBreak));
      }
    };
  }
}
