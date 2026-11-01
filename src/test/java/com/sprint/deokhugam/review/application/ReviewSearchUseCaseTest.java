package com.sprint.deokhugam.review.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.common.pagination.CursorPageResponse;
import com.sprint.deokhugam.review.application.dto.req.ReviewSearchCondition;
import com.sprint.deokhugam.review.application.dto.res.ReviewResponse;
import com.sprint.deokhugam.review.application.required.ReviewLikeQueryPort;
import com.sprint.deokhugam.review.application.required.ReviewQueryPort;
import com.sprint.deokhugam.review.domain.Review;
import com.sprint.deokhugam.user.domain.Email;
import com.sprint.deokhugam.user.domain.Password;
import com.sprint.deokhugam.user.domain.User;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ReviewSearchUseCaseTest {
  @Mock ReviewLikeQueryPort reviewLikeQueryPort;
  @Mock ReviewQueryPort reviewQueryPort;
  @InjectMocks ReviewApplicationService service;

  @Test
  void 리뷰_커서기반목록조회_좋아요_전체개수_검증() {
    // given
    UUID requestUserId = UUID.randomUUID();
    UUID reviewId = UUID.randomUUID();
    ReviewSearchCondition condition =
        new ReviewSearchCondition(null, null, null, "createdAt", "DESC", null, null, 10);

    User user = User.create("작성자", Email.to("tester@example.com"), Password.to("password"));
    Book book = Book.create("책", "저자", "소개", "출판사", LocalDate.of(2020, 1, 1), null, null);
    Review review = Review.create(4, "좋아요", user, book);
    ReflectionTestUtils.setField(user, "id", UUID.randomUUID());
    ReflectionTestUtils.setField(book, "id", UUID.randomUUID());
    ReflectionTestUtils.setField(review, "id", reviewId);
    ReflectionTestUtils.setField(review, "createdAt", Instant.parse("2026-10-23T00:00:00Z"));

    when(reviewQueryPort.search(condition)).thenReturn(List.of(review));
    when(reviewQueryPort.count(condition)).thenReturn(1L);
    when(reviewLikeQueryPort.findReviewLikeIdsByRequestId(List.of(reviewId), requestUserId))
        .thenReturn(List.of(reviewId));

    // when
    CursorPageResponse<ReviewResponse> result =
        service.searchReviewsByPagination(condition, requestUserId);

    // then
    assertThat(result.content()).hasSize(1);
    assertThat(result.content().getFirst().id()).isEqualTo(reviewId);
    assertThat(result.content().getFirst().likedByMe()).isTrue();
    assertThat(result.totalElements()).isEqualTo(1);
    assertThat(result.hasNext()).isFalse();
  }
}
