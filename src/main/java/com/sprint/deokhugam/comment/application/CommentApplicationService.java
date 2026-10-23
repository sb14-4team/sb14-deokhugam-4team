package com.sprint.deokhugam.comment.application;

import com.sprint.deokhugam.comment.application.dto.CommentCreateRequest;
import com.sprint.deokhugam.comment.application.dto.CommentDto;
import com.sprint.deokhugam.comment.application.dto.CommentUpdateRequest;
import com.sprint.deokhugam.comment.application.provided.CommentUsecase;
import com.sprint.deokhugam.comment.application.provided.command.CommentCommand;
import com.sprint.deokhugam.comment.application.provided.query.CommentFinder;
import com.sprint.deokhugam.comment.domain.Comment;
import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.domain.comment.exception.CommentErrorCode;
import com.sprint.deokhugam.review.application.provided.query.ReviewFinder;
import com.sprint.deokhugam.review.domain.Review;
import com.sprint.deokhugam.user.application.provided.query.UserFinder;
import com.sprint.deokhugam.user.domain.User;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentApplicationService implements CommentUsecase {

  private final CommentCommand commentCommand;
  private final CommentFinder commentFinder;

  private final ReviewFinder reviewFinder;
  private final UserFinder userFinder;

  @Override
  @Transactional
  public CommentDto create(CommentCreateRequest createRequest) {
    Review review = reviewFinder.getByIdOrThrow(createRequest.reviewId());
    User user = userFinder.findById(createRequest.userId());

    Comment comment = Comment.create(review, user, createRequest.content());
    Comment savedComment = commentCommand.createComment(comment);

    return CommentDto.from(savedComment);
  }

  @Override
  @Transactional
  public CommentDto update(UUID commentId, UUID requestUserId, CommentUpdateRequest updateRequest) {
    getVerifiedComment(commentId, requestUserId);
    Comment updatedComment = commentCommand.updateComment(commentId, requestUserId, updateRequest);

    return CommentDto.from(updatedComment);
  }

  @Override
  @Transactional
  public void delete(UUID commentId, UUID userId) {
    getVerifiedComment(commentId, userId);
    Comment foundComment = commentFinder.findById(commentId);
    foundComment.softDelete();
  }

  private void getVerifiedComment(UUID commentId, UUID userId) {
    Comment comment = commentFinder.findById(commentId);
    if (!comment.verifyCommentId(userId)) {
      throw new BusinessException(CommentErrorCode.FORBIDDEN);
    }
  }
}
