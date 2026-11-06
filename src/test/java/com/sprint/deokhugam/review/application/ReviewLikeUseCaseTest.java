package com.sprint.deokhugam.review.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.notification.domain.NotifyEvent;
import com.sprint.deokhugam.review.application.dto.res.ReviewLikeResponse;
import com.sprint.deokhugam.review.application.required.ReviewLikeRepositoryPort;
import com.sprint.deokhugam.review.application.required.ReviewRepositoryPort;
import com.sprint.deokhugam.review.domain.Review;
import com.sprint.deokhugam.review.domain.ReviewLike;
import com.sprint.deokhugam.review.domain.exception.ReviewErrorCode;
import com.sprint.deokhugam.user.application.provided.query.UserFinder;
import com.sprint.deokhugam.user.domain.Email;
import com.sprint.deokhugam.user.domain.Password;
import com.sprint.deokhugam.user.domain.User;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ReviewLikeUseCaseTest {

  @Mock ReviewLikeRepositoryPort reviewLikeRepositoryPort;
  @Mock ReviewRepositoryPort reviewRepositoryPort;
  @Mock UserFinder userFinder;
  @Mock org.springframework.context.ApplicationEventPublisher eventPublisher;

  @InjectMocks ReviewApplicationService reviewApplicationService;

  private UUID userId;
  private UUID reviewId;
  private User user;
  private Review review;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    reviewId = UUID.randomUUID();

    user = User.create("jh", Email.to("sdadsa@naver.com"), Password.to("password"));

    Book book = Book.create("제목", "저자", null, null, null, null, null);

    review = Review.create(4, "좋아요", user, book);

    ReflectionTestUtils.setField(user, "id", userId);
    ReflectionTestUtils.setField(review, "id", reviewId);
  }

  @Test
  void 리뷰좋아요_새로_생성될경우() {
    // given
    when(userFinder.findByIdOrThrow(userId)).thenReturn(user);
    when(reviewRepositoryPort.getByIdForUpdateOrThrow(reviewId)).thenReturn(review);
    when(reviewLikeRepositoryPort.getByUserIdAndReviewId(userId, reviewId))
        .thenReturn(Optional.empty());

    // when
    ReviewLikeResponse response = reviewApplicationService.toggleLike(reviewId, userId);

    // then
    assertThat(response.liked()).isTrue();
    assertThat(response.reviewId()).isEqualTo(reviewId);
    assertThat(response.userId()).isEqualTo(userId);
    assertThat(review.getLikeCount()).isEqualTo(1);
    verify(reviewLikeRepositoryPort).save(any(ReviewLike.class));
    verify(eventPublisher).publishEvent(NotifyEvent.like(reviewId, userId, user.getNickname()));
  }

  @Test
  void 리뷰좋아요_토클확인_감소확인() {
    // given
    ReviewLike existing = ReviewLike.init(review, user);
    review.increaseLikeCount();

    when(userFinder.findByIdOrThrow(userId)).thenReturn(user);
    when(reviewRepositoryPort.getByIdForUpdateOrThrow(reviewId)).thenReturn(review);
    when(reviewLikeRepositoryPort.getByUserIdAndReviewId(userId, reviewId))
        .thenReturn(Optional.of(existing));

    // when
    ReviewLikeResponse response = reviewApplicationService.toggleLike(reviewId, userId);

    // then
    assertThat(response.liked()).isFalse();
    assertThat(existing.isLiked()).isFalse();
    assertThat(review.getLikeCount()).isZero();
    verify(reviewLikeRepositoryPort, never()).save(any(ReviewLike.class));
    verifyNoInteractions(eventPublisher);
  }

  @Test
  void 취소한_좋아요를_다시_누르면_기존_기록을_활성화한다() {
    // given
    ReviewLike existing = ReviewLike.init(review, user);
    existing.toggle();
    when(userFinder.findByIdOrThrow(userId)).thenReturn(user);
    when(reviewRepositoryPort.getByIdForUpdateOrThrow(reviewId)).thenReturn(review);
    when(reviewLikeRepositoryPort.getByUserIdAndReviewId(userId, reviewId))
        .thenReturn(Optional.of(existing));

    // when
    ReviewLikeResponse response = reviewApplicationService.toggleLike(reviewId, userId);

    // then
    assertThat(response.liked()).isTrue();
    assertThat(existing.isLiked()).isTrue();
    assertThat(review.getLikeCount()).isEqualTo(1);
    verify(reviewLikeRepositoryPort, never()).save(any(ReviewLike.class));
    verify(eventPublisher).publishEvent(NotifyEvent.like(reviewId, userId, user.getNickname()));
  }

  @Test
  void 리뷰가_없으면_좋아요를_저장하거나_이벤트를_발행하지_않는다() {
    // given
    when(userFinder.findByIdOrThrow(userId)).thenReturn(user);
    when(reviewRepositoryPort.getByIdForUpdateOrThrow(reviewId))
        .thenThrow(new BusinessException(ReviewErrorCode.NOT_FOUND));

    // when
    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> reviewApplicationService.toggleLike(reviewId, userId));

    // then
    assertThat(exception.getErrorCode()).isEqualTo(ReviewErrorCode.NOT_FOUND);
    assertThat(review.getLikeCount()).isZero();
    verifyNoInteractions(reviewLikeRepositoryPort, eventPublisher);
  }

  @Test
  void 리뷰좋아요_감소_음수실퍠() {
    // given
    assertThat(review.getLikeCount()).isZero();

    // when
    BusinessException exception = assertThrows(BusinessException.class, review::decreaseLikeCount);

    // then
    assertThat(exception.getErrorCode()).isEqualTo(ReviewErrorCode.LIKE_COUNT_UNDERFLOW);
    assertThat(review.getLikeCount()).isZero();
  }
}
