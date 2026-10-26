package com.sprint.deokhugam.comment.application;

import com.sprint.deokhugam.comment.application.dto.CommentCreateRequest;
import com.sprint.deokhugam.comment.application.dto.CommentDto;
import com.sprint.deokhugam.comment.application.dto.CommentSearchCondition;
import com.sprint.deokhugam.comment.application.dto.CommentUpdateRequest;
import com.sprint.deokhugam.comment.application.provided.CommentUsecase;
import com.sprint.deokhugam.comment.application.provided.command.CommentCommand;
import com.sprint.deokhugam.comment.application.provided.query.CommentFinder;
import com.sprint.deokhugam.comment.application.required.CommentSearchPort;
import com.sprint.deokhugam.comment.domain.Comment;
import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.common.pagination.CursorPageResponse;
import com.sprint.deokhugam.domain.comment.exception.CommentErrorCode;
import com.sprint.deokhugam.review.application.provided.query.ReviewFinder;
import com.sprint.deokhugam.review.domain.Review;
import com.sprint.deokhugam.user.application.provided.query.UserFinder;
import com.sprint.deokhugam.user.domain.User;
import java.util.List;
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

  private final CommentSearchPort commentSearchPort;

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
    Comment comment = getVerifiedComment(commentId, requestUserId);
    Comment updatedComment =
        commentCommand.updateComment(comment.getId(), comment.getUser().getId(), updateRequest);

    return CommentDto.from(updatedComment);
  }

  @Override
  @Transactional
  public void softDelete(UUID commentId, UUID userId) {
    Comment comment = getVerifiedComment(commentId, userId);
    comment.softDelete();
  }

  @Override
  @Transactional
  public void hardDelete(UUID commentId, UUID userId) {
    Comment comment = getVerifiedComment(commentId, userId);
    commentCommand.hardDelete(comment.getId());
  }

  private Comment getVerifiedComment(UUID commentId, UUID userId) {
    Comment comment = commentFinder.findById(commentId);
    if (!comment.verifyCommentId(userId)) {
      throw new BusinessException(CommentErrorCode.FORBIDDEN);
    }
    return comment;
  }

  @Override
  @Transactional(readOnly = true)
  public CursorPageResponse<CommentDto> searchCommentsByPagination(
      CommentSearchCondition condition) {
    reviewFinder.requireActiveById(condition.reviewId());

    List<Comment> commentList = commentSearchPort.search(condition);
    long totalElements = commentSearchPort.count(condition);

    List<CommentDto> responses = commentList.stream().map(CommentDto::from).toList();

    return toCursorPage(responses, condition, totalElements);
  }

  private CursorPageResponse<CommentDto> toCursorPage(
      List<CommentDto> responses, CommentSearchCondition condition, long totalElements) {
    return CursorPageResponse.of(
        responses, condition.limit(), CommentDto::createAt, CommentDto::createAt, totalElements);
  }
}
