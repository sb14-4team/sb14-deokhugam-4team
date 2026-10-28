package com.sprint.deokhugam.review.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.domain.review.exception.ReviewErrorCode;
import com.sprint.deokhugam.review.application.provided.command.ReviewCommand;
import com.sprint.deokhugam.review.application.provided.query.ReviewFinder;
import com.sprint.deokhugam.review.domain.Review;
import com.sprint.deokhugam.user.domain.Email;
import com.sprint.deokhugam.user.domain.Password;
import com.sprint.deokhugam.user.domain.User;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

// 통합테스트
@SpringBootTest
@Transactional
class ReviewApplicationServiceTest {
  @Autowired ReviewCommand reviewCommand;
  @Autowired ReviewFinder reviewFinder;
  @Autowired EntityManager em;

  private User user;
  private Book book;
  private Review review;

  @BeforeEach
  void setUp() {
    user =
        User.create(
            "tester", Email.to(UUID.randomUUID() + "@test.com"), Password.to("test-password"));
    book = Book.create("제목", "저자", "소개", "출판사", LocalDate.of(2020, 1, 1), null, null);

    em.persist(user);
    em.persist(book);
  }

  @Test
  void 리뷰_디비_정상저장확인() {
    Review saved = reviewCommand.createReview(Review.create(3, "성공", user, book));
    em.flush();
    em.clear();

    Review found = reviewFinder.getByIdOrThrow(saved.getId());

    assertThat(found.getRating()).isEqualTo(3);
    assertThat(found.getContent()).isEqualTo("성공");
    assertThat(found.getUser().getId()).isEqualTo(user.getId());
    assertThat(found.getBook().getId()).isEqualTo(book.getId()); // 널임

    // 리뷰가 달리면 도서 집계도 같은 트랜잭션에서 바뀐다
    Book foundBook = em.find(Book.class, book.getId());
    assertThat(foundBook.getReviewCount()).isEqualTo(1);
    assertThat(foundBook.getRating()).isEqualTo(3.0);
  }

  @Test
  void 리뷰_디비_중복저장_실패확인() {
    Review saved = reviewCommand.createReview(Review.create(3, "성공", user, book));
    em.flush();
    em.clear();

    assertThat(
            assertThrows(
                    BusinessException.class,
                    () -> reviewCommand.createReview(Review.create(4, "중복 리뷰", user, book)))
                .getErrorCode())
        .isEqualTo(ReviewErrorCode.ALREADY_EXISTS);
  }

  @Test
  void 리뷰_디비_정상업데이트확인() {
    Review saved = reviewCommand.createReview(Review.create(3, "성공", user, book));
    em.flush();
    UUID reviewId = saved.getId();
    em.clear();

    reviewCommand.updateReview(reviewId, user.getId(), "", 1);
    em.flush();
    em.clear();

    Review found = reviewFinder.getByIdOrThrow(reviewId);

    assertThat(found.getId()).isEqualTo(reviewId);
    assertThat(found.getContent()).isEmpty();
    assertThat(found.getRating()).isEqualTo(1);
  }
}
