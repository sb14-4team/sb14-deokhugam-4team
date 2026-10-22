package com.sprint.deokhugam.comment.application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.sprint.deokhugam.comment.application.dto.CommentCreateRequest;
import com.sprint.deokhugam.comment.application.dto.CommentDto;
import com.sprint.deokhugam.comment.application.required.CommentRepository;
import com.sprint.deokhugam.comment.domain.Comment;
import com.sprint.deokhugam.review.application.required.ReviewPersistencePort;
import com.sprint.deokhugam.review.domain.Review;
import com.sprint.deokhugam.user.application.required.UserRepository;
import com.sprint.deokhugam.user.domain.User;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CommentCommandServiceTest {

  @Mock private CommentRepository commentRepository;
  @Mock private UserRepository userRepository;
  @Mock private ReviewPersistencePort reviewPersistencePort;
  @Mock private Review review;
  @Mock private User user;
  @InjectMocks private CommentCommandService commandService;

  private UUID reviewId;
  private UUID userId;

  @BeforeEach
  void setUp() {
    reviewId = UUID.randomUUID();
    userId = UUID.randomUUID();

    // given(review.getId()).willReturn(reviewId);
    // given(user.getId()).willReturn(userId);
  }

  @Test
  @DisplayName("댓글 등록 성공")
  void createComment_success() {
    // Given
    CommentCreateRequest createRequest =
        new CommentCreateRequest(reviewId, userId, "testCreateContent");

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(reviewPersistencePort.getByIdOrThrow(reviewId)).willReturn(review);

    Comment comment = Comment.create(review, user, createRequest.content());

    given(commentRepository.save(any(Comment.class))).willReturn(comment);

    // When
    CommentDto result = commandService.create(createRequest);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.content()).isEqualTo("testCreateContent");
    verify(commentRepository).save(any(Comment.class));
  }
}
