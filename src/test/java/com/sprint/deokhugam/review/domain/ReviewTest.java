package com.sprint.deokhugam.review.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.review.domain.exception.ReviewErrorCode;
import com.sprint.deokhugam.user.domain.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class ReviewTest {

  @ParameterizedTest
  @CsvSource({"1, 0", "5, 200"})
  void 평점과_내용의_허용_경계값으로_리뷰를_생성한다(int rating, int contentLength) {
    // given
    User user = mock(User.class);
    Book book = mock(Book.class);
    String content = "가".repeat(contentLength);

    // when
    Review review = Review.create(rating, content, user, book);

    // then
    assertThat(review.getRating()).isEqualTo(rating);
    assertThat(review.getContent()).isEqualTo(content);
    assertThat(review.getUser()).isSameAs(user);
    assertThat(review.getBook()).isSameAs(book);
    assertThat(review.getLikeCount()).isZero();
    assertThat(review.getCommentCount()).isZero();
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(ints = {0, 6})
  void 평점이_없거나_허용범위를_벗어나면_생성할_수_없다(Integer rating) {
    // given
    User user = mock(User.class);
    Book book = mock(Book.class);

    // when
    BusinessException exception =
        assertThrows(BusinessException.class, () -> Review.create(rating, "내용", user, book));

    // then
    assertThat(exception.getErrorCode()).isEqualTo(ReviewErrorCode.INVALID_RATING);
  }

  @Test
  void 내용이_null이면_생성할_수_없다() {
    // given
    User user = mock(User.class);
    Book book = mock(Book.class);

    // when
    BusinessException exception =
        assertThrows(BusinessException.class, () -> Review.create(3, null, user, book));

    // then
    assertThat(exception.getErrorCode()).isEqualTo(ReviewErrorCode.INVALID_CONTENT);
  }

  @Test
  void 내용이_201자이면_생성할_수_없다() {
    // given
    User user = mock(User.class);
    Book book = mock(Book.class);
    String content = "가".repeat(201);

    // when
    BusinessException exception =
        assertThrows(BusinessException.class, () -> Review.create(3, content, user, book));

    // then
    assertThat(exception.getErrorCode()).isEqualTo(ReviewErrorCode.INVALID_CONTENT);
  }

  @Test
  void 잘못된_내용으로_수정하면_기존_내용과_평점이_유지된다() {
    // given
    Review review = Review.create(3, "기존 내용", mock(User.class), mock(Book.class));

    // when
    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> review.updateContentAndRating("가".repeat(201), 5));

    // then
    assertThat(exception.getErrorCode()).isEqualTo(ReviewErrorCode.INVALID_CONTENT);
    assertThat(review.getContent()).isEqualTo("기존 내용");
    assertThat(review.getRating()).isEqualTo(3);
  }
}
