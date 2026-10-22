package com.sprint.deokhugam.review.adapter.persistence;

import static com.sprint.deokhugam.review.domain.QReview.review;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.common.exception.CommonErrorCode;
import com.sprint.deokhugam.review.application.dto.req.ReviewSearchCondition;
import com.sprint.deokhugam.review.domain.Review;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class ReviewQuerydslRepository {
  private final JPAQueryFactory queryFactory;

  public List<Review> search(ReviewSearchCondition condition) {
    return queryFactory
        .selectFrom(review)
        .where(
            review.deletedAt.isNull(),
            keywordCondition(condition.keyword()),
            userIdCondition(condition.userId()),
            bookIdCondition(condition.bookId()))
        .orderBy(getOrderSpecifiers(condition.orderBy(), condition.direction()))
        .limit(condition.limit() + 1L)
        .fetch();
  }

  /*
     like -> eq : equals  contains -> contains
     결과 :   like 검색조건   like %{검색 조건}%
  */
  private BooleanExpression keywordCondition(String keyword) {
    if (keyword == null || keyword.isBlank()) return null;

    String value = keyword.replaceAll("\\s+", "");
    return review
        .user
        .nickname
        .containsIgnoreCase(value)
        .or(review.content.containsIgnoreCase(value));
  }

  private BooleanExpression userIdCondition(UUID userId) {
    return userId == null ? null : review.user.id.eq(userId);
  }

  private BooleanExpression bookIdCondition(UUID bookId) {
    return bookId == null ? null : review.book.id.eq(bookId);
  }

  // 정렬 조건 - 기본값 생성일 / 내림차순
  private OrderSpecifier<?>[] getOrderSpecifiers(
      String conditionOrderBy, String conditionDirection) {
    String orderBy = conditionOrderBy == null ? "createdAt" : conditionOrderBy; // 기본 정렬 조건 - 생성일

    Order direction = sortDirection(conditionDirection);
    OrderSpecifier<?> idOrder = new OrderSpecifier<>(direction, review.id);
    OrderSpecifier<?> createdAtOrder = new OrderSpecifier<>(direction, review.createdAt);
    OrderSpecifier<?> ratingOrder = new OrderSpecifier<>(direction, review.rating);

    return switch (orderBy) {
      case "createdAt" -> new OrderSpecifier<?>[] {createdAtOrder, idOrder};
      case "rating" -> new OrderSpecifier<?>[] {ratingOrder, idOrder};
      default ->
          throw new BusinessException(
              CommonErrorCode.INVALID_INPUT, "잘못된 정렬 조건값 => " + conditionOrderBy);
    };
  }

  private Order sortDirection(String direction) {
    if (direction == null) return Order.DESC; // 내림차순 기본값

    return switch (direction.toUpperCase(Locale.ROOT)) {
      case "ASC" -> Order.ASC;
      case "DESC" -> Order.DESC;
      default ->
          throw new BusinessException(CommonErrorCode.INVALID_INPUT, "direction=" + direction);
    };
  }
}
