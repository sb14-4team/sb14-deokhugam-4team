package com.sprint.deokhugam.review.application;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.deokhugam.book.application.provided.command.BookReviewStatsCommand;
import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.review.application.required.ReviewLikeRepositoryPort;
import com.sprint.deokhugam.review.application.required.ReviewRepositoryPort;
import com.sprint.deokhugam.review.domain.Review;
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
  @Mock BookReviewStatsCommand bookReviewStatsCommand;
  @InjectMocks ReviewApplicationService service;

  private UUID userId;
  private UUID reviewId;
  private Book book;
  private Review review;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    reviewId = UUID.randomUUID();
    User user = mock(User.class);
    book = mock(Book.class);
    review = Review.create(3, "내용", user, book);

    when(user.getId()).thenReturn(userId);
    when(reviewRepositoryPort.getByIdIncludingDeleted(reviewId)).thenReturn(Optional.of(review));
  }

  @Test
  void 활성_리뷰를_물리삭제하면_도서_집계에서_뺀다() {
    service.hardDeleteReview(reviewId, userId);

    verify(bookReviewStatsCommand).removeReview(book, 3);
    verify(reviewRepositoryPort).delete(review);
  }

  @Test
  void 이미_논리삭제된_리뷰는_집계를_다시_빼지_않는다() {
    review.softDelete();

    service.hardDeleteReview(reviewId, userId);

    verify(bookReviewStatsCommand, never()).removeReview(book, 3);
    verify(reviewRepositoryPort).delete(review);
  }
}
