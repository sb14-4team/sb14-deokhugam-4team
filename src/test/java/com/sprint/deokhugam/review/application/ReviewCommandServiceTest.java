package com.sprint.deokhugam.review.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.domain.review.exception.ReviewErrorCode;
import com.sprint.deokhugam.review.application.required.ReviewPersistencePort;
import com.sprint.deokhugam.review.domain.Review;
import com.sprint.deokhugam.user.domain.User;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

// 단위테스트 용
@Slf4j
class ReviewCommandServiceTest {

  private ReviewPersistencePort reviewPersistencePort;
  private ReviewCommandService reviewCommandService;
  private UUID userId;
  private UUID bookId;
  private Review review;

  @BeforeEach
  void setUp() {
    reviewPersistencePort = mock(ReviewPersistencePort.class);
    reviewCommandService = new ReviewCommandService(reviewPersistencePort);

    userId = UUID.randomUUID();
    bookId = UUID.randomUUID();
    User user = mock(User.class);
    Book book = mock(Book.class);
    when(user.getId()).thenReturn(userId);
    when(book.getId()).thenReturn(bookId);
    review = Review.init(5, "굳", user, book);
  }

  @Test
  void 리뷰저장_호출확인() {
    when(reviewPersistencePort.save(review)).thenReturn(review);

    Review savedReview = reviewCommandService.createReview(review);

    assertThat(savedReview).isSameAs(review);
    assertThat(savedReview.getRating()).isEqualTo(5);
    verify(reviewPersistencePort).existsByUserIdAndBookId(userId, bookId);
    verify(reviewPersistencePort).save(review);
  }

  @Test
  void 객체의_연관관계가_널이면_실패() {
    Review 유저가없는리뷰 = Review.init(5, "재미있어요", null, review.getBook());
    Review 도서가없는리뷰 = Review.init(5, "재미있어요", review.getUser(), null);

    assertThatThrownBy(() -> reviewCommandService.createReview(유저가없는리뷰)).hasMessage("review.user");
    assertThatThrownBy(() -> reviewCommandService.createReview(도서가없는리뷰)).hasMessage("review.book");
  }

  @Test
  void 유저와도서가_중복된아이가있으면_실행안함() {
    when(reviewPersistencePort.existsByUserIdAndBookId(userId, bookId)).thenReturn(true);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> reviewCommandService.createReview(review));

    assertThat(exception.getErrorCode()).isEqualTo(ReviewErrorCode.ALREADY_EXISTS);
    verify(reviewPersistencePort, never()).save(review);
  }

  @Test
  void 리뷰_수정_정상동작확인() {
    UUID reviewId = UUID.randomUUID();
    when(reviewPersistencePort.getByIdOrThrow(reviewId)).thenReturn(review);
    log.info("리뷰 id - {}, 리뷰 내용 - {}", review.getId(), review.getContent());

    Review updated = reviewCommandService.updateReview(reviewId, userId, "수정한 내용", 4);

    assertThat(updated).isSameAs(review);
    assertThat(updated.getContent()).isEqualTo("수정한 내용");
    assertThat(updated.getRating()).isEqualTo(4);
    verify(reviewPersistencePort).getByIdOrThrow(reviewId);
  }

  @Test
  void 리뷰_주인작성자_아니면실패() {
    UUID reviewId = UUID.randomUUID();
    when(reviewPersistencePort.getByIdOrThrow(reviewId)).thenReturn(review);

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> reviewCommandService.updateReview(reviewId, UUID.randomUUID(), "수정한 내용", 4));

    assertThat(exception.getErrorCode()).isEqualTo(ReviewErrorCode.FORBIDDEN);
    assertThat(review.getContent()).isEqualTo("굳");
    assertThat(review.getRating()).isEqualTo(5);
  }
}
