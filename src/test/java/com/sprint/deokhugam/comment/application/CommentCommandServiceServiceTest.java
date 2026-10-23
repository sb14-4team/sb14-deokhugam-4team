package com.sprint.deokhugam.comment.application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.sprint.deokhugam.comment.application.dto.CommentCreateRequest;
import com.sprint.deokhugam.comment.application.dto.CommentDto;
import com.sprint.deokhugam.comment.application.provided.command.CommentCommand;
import com.sprint.deokhugam.comment.application.provided.query.CommentFinder;
import com.sprint.deokhugam.comment.domain.Comment;
import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.domain.user.exception.UserErrorCode;
import com.sprint.deokhugam.review.application.provided.query.ReviewFinder;
import com.sprint.deokhugam.review.domain.Review;
import com.sprint.deokhugam.user.application.provided.query.UserFinder;
import com.sprint.deokhugam.user.domain.User;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CommentCommandServiceServiceTest {

  @Mock private CommentCommand commentCommand;
  @Mock private CommentFinder commentFinder;
  @Mock private UserFinder userFinder;
  @Mock private ReviewFinder reviewFinder;
  @Mock private Review review;
  @Mock private User user;

  @InjectMocks private CommentApplicationService commentApplicationService;

  private UUID reviewId;
  private UUID userId;
  private CommentCreateRequest createRequest;

  @BeforeEach
  void setUp() {
    reviewId = UUID.randomUUID();
    userId = UUID.randomUUID();
    createRequest = new CommentCreateRequest(reviewId, userId, "testCreateContent");

    // given(review.getId()).willReturn(reviewId);
    // given(user.getId()).willReturn(userId);
  }

  @Test
  @DisplayName("댓글 등록 성공")
  void createComment_success() {
    // Given
    given(reviewFinder.getByIdOrThrow(reviewId)).willReturn(review);
    given(userFinder.findById(userId)).willReturn(user);

    Comment comment = Comment.create(review, user, createRequest.content());
    given(commentCommand.createComment(any(Comment.class))).willReturn(comment);

    // When
    CommentDto result = commentApplicationService.create(createRequest);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.content()).isEqualTo("testCreateContent");
    verify(commentCommand).createComment(any(Comment.class));
  }

  @Test
  @DisplayName("댓글 등록 실패 - 유저를 찾지 못한 경우")
  void createComment_fail_userNotFound() {
    // Given
    given(userFinder.findById(userId)).willThrow(new BusinessException(UserErrorCode.NOT_FOUND));
    given(reviewFinder.getByIdOrThrow(reviewId)).willReturn(review);

    // When & Then
    assertThatThrownBy(() -> commentApplicationService.create(createRequest))
        .isInstanceOf(BusinessException.class)
        .satisfies(
            exception -> {
              BusinessException businessException = (BusinessException) exception;
              assertThat(businessException.getErrorCode()).isEqualTo(UserErrorCode.NOT_FOUND);
            });
  }
}
