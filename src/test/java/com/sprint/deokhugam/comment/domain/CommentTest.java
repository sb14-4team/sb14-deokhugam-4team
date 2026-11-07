package com.sprint.deokhugam.comment.domain;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.common.exception.CommonErrorCode;
import com.sprint.deokhugam.review.domain.Review;
import com.sprint.deokhugam.user.domain.User;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// Mockito 기능 확장
@ExtendWith(MockitoExtension.class)
public class CommentTest {

  @Mock private Review review;
  @Mock private User user;

  @Test
  @DisplayName("댓글 내용 수정 성공")
  void update_success() {
    // Given
    Comment comment = Comment.create(review, user, "testOriginContent");

    // When
    comment.update("testUpdatedContent");

    // Then
    assertThat(comment.getContent()).isEqualTo("testUpdatedContent");
  }

  // 파라미터를 바꾸어가며 여러 번 실행되는 테스트
  @ParameterizedTest
  // 파라미터에 null값을 주입
  @NullSource
  // 파라미터에 빈 문자열, 공백 문자열 주입
  @ValueSource(strings = {"", "   "})
  @DisplayName("댓글 내용 수정 실패 - 내용이 비어 있으면 예외 발생")
  void update_fail_blankContent(String blankContent) {
    // Given
    Comment comment = Comment.create(review, user, "testOriginContent");

    // When & Then
    assertThatThrownBy(() -> comment.update(blankContent))
        .isInstanceOf(BusinessException.class)
        .extracting("errorCode")
        .isEqualTo(CommonErrorCode.INVALID_INPUT);

    assertThat(comment.getContent()).isEqualTo("testOriginContent");
  }

  @Test
  @DisplayName("작성자 확인 - 작성자 ID면 true")
  void isWrittenBy_writer() {
    // Given
    UUID userId = UUID.randomUUID();
    given(user.getId()).willReturn(userId);
    Comment comment = Comment.create(review, user, "testCreateContent");

    // When & Then
    assertThat(comment.isWrittenBy(userId)).isTrue();
  }

  @Test
  @DisplayName("작성자 확인 - 다른 유저 ID면 false")
  void isWrittenBy_otherUser() {
    // Given
    given(user.getId()).willReturn(UUID.randomUUID());
    Comment comment = Comment.create(review, user, "testCreateContent");

    // When & Then
    assertThat(comment.isWrittenBy(UUID.randomUUID())).isFalse();
  }
}
