package com.sprint.deokhugam.review.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.sprint.deokhugam.book.application.provided.command.BookReviewStatsCommand;
import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.comment.application.provided.CommentUseCase;
import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.review.application.required.ReviewLikeRepositoryPort;
import com.sprint.deokhugam.review.application.required.ReviewRepositoryPort;
import com.sprint.deokhugam.review.domain.Review;
import com.sprint.deokhugam.review.domain.exception.ReviewErrorCode;
import com.sprint.deokhugam.user.application.provided.query.UserFinder;
import com.sprint.deokhugam.user.domain.User;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReviewApplicationServiceHardDeleteTest {
  @Mock UserFinder userFinder;
  @Mock ReviewRepositoryPort reviewRepositoryPort;
  @Mock ReviewLikeRepositoryPort reviewLikeRepositoryPort;
  @Mock CommentUseCase commentUseCase;
  @Mock BookReviewStatsCommand bookReviewStatsCommand;
  @InjectMocks ReviewApplicationService service;

  private UUID userId;
  private UUID reviewId;
  private Book book;
  private Review review;
  private User user;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    reviewId = UUID.randomUUID();
    user = mock(User.class);
    book = mock(Book.class);
    review = Review.create(3, "내용", user, book);
  }

  @Test
  void 활성_리뷰를_물리삭제하면_도서_집계에서_뺀다() {
    // given
    when(user.getId()).thenReturn(userId);
    when(reviewRepositoryPort.getByIdIncludingDeleted(reviewId)).thenReturn(Optional.of(review));

    // when
    service.hardDeleteReview(reviewId, userId);

    // then
    verify(bookReviewStatsCommand).removeReview(book, 3);
    verify(reviewRepositoryPort).delete(review);
  }

  @Test
  void 이미_논리삭제된_리뷰는_집계를_다시_빼지_않는다() {
    // given
    when(user.getId()).thenReturn(userId);
    when(reviewRepositoryPort.getByIdIncludingDeleted(reviewId)).thenReturn(Optional.of(review));
    review.softDelete();

    // when
    service.hardDeleteReview(reviewId, userId);

    // then
    verify(bookReviewStatsCommand, never()).removeReview(book, 3);
    verify(reviewRepositoryPort).delete(review);
  }

  @Test
  void 리뷰가_없으면_물리삭제하지_않는다() {
    // given
    when(reviewRepositoryPort.getByIdIncludingDeleted(reviewId)).thenReturn(Optional.empty());

    // when
    BusinessException exception =
        assertThrows(BusinessException.class, () -> service.hardDeleteReview(reviewId, userId));

    // then
    assertThat(exception.getErrorCode()).isEqualTo(ReviewErrorCode.NOT_FOUND);
    verifyNoInteractions(commentUseCase, reviewLikeRepositoryPort, bookReviewStatsCommand);
    verify(reviewRepositoryPort, never()).delete(review);
  }

  @Test
  void 작성자가_아니면_리뷰와_관련_데이터를_삭제하지_않는다() {
    // given
    when(user.getId()).thenReturn(userId);
    when(reviewRepositoryPort.getByIdIncludingDeleted(reviewId)).thenReturn(Optional.of(review));
    UUID otherUserId = UUID.randomUUID();

    // when
    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> service.hardDeleteReview(reviewId, otherUserId));

    // then
    assertThat(exception.getErrorCode()).isEqualTo(ReviewErrorCode.FORBIDDEN);
    verifyNoInteractions(commentUseCase, reviewLikeRepositoryPort, bookReviewStatsCommand);
    verify(reviewRepositoryPort, never()).delete(review);
  }
}
