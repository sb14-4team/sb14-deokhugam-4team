package com.sprint.deokhugam.comment.application;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.sprint.deokhugam.comment.application.dto.CommentUpdateRequest;
import com.sprint.deokhugam.comment.application.required.CommentPersistencePort;
import com.sprint.deokhugam.comment.domain.Comment;
import com.sprint.deokhugam.review.domain.Review;
import com.sprint.deokhugam.user.domain.User;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CommentCommandServiceTest {

  @Mock private CommentPersistencePort commentPersistencePort;
  @Mock private Review review;
  @Mock private User user;

  @InjectMocks private CommentCommandService commentCommandService;

  @Test
  @DisplayName("댓글 내용 수정 성공")
  void updateComment_success() {
    // Given
    UUID commentId = UUID.randomUUID();
    Comment comment = Comment.create(review, user, "testOriginContent");
    given(commentPersistencePort.findById(commentId)).willReturn(comment);

    // When
    Comment result =
        commentCommandService.updateComment(
            commentId, new CommentUpdateRequest("testUpdatedContent"));

    // Then
    assertThat(result.getContent()).isEqualTo("testUpdatedContent");
  }

  // 어떤 조회를 사용하는지 확인하기 위해 테스트
  @Test
  @DisplayName("댓글 물리 삭제 성공 - 논리 삭제된 댓글까지 찾아서 삭제한다")
  void hardDelete_success() {
    // Given
    UUID commentId = UUID.randomUUID();
    Comment comment = Comment.create(review, user, "testCreateContent");
    given(commentPersistencePort.findByIdIncludingDeleted(commentId)).willReturn(comment);

    // When
    commentCommandService.hardDelete(commentId);

    // Then
    verify(commentPersistencePort).delete(comment);
  }
}
