package com.sprint.deokhugam.comment.application;

import com.sprint.deokhugam.comment.application.dto.CommentCreateRequest;
import com.sprint.deokhugam.comment.application.dto.CommentDto;
import com.sprint.deokhugam.comment.application.dto.CommentSearchCondition;
import com.sprint.deokhugam.comment.application.dto.CommentUpdateRequest;
import com.sprint.deokhugam.comment.application.provided.CommentUseCase;
import com.sprint.deokhugam.comment.application.provided.command.CommentCommand;
import com.sprint.deokhugam.comment.application.provided.query.CommentFinder;
import com.sprint.deokhugam.comment.application.required.CommentSearchPort;
import com.sprint.deokhugam.comment.domain.Comment;
import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.common.exception.CommonErrorCode;
import com.sprint.deokhugam.common.pagination.CursorPageResponse;
import com.sprint.deokhugam.domain.comment.exception.CommentErrorCode;
import com.sprint.deokhugam.notification.application.provided.command.NotificationCommand;
import com.sprint.deokhugam.notification.domain.NotifyEvent;
import com.sprint.deokhugam.review.application.provided.command.ReviewCommand;
import com.sprint.deokhugam.review.application.provided.query.ReviewFinder;
import com.sprint.deokhugam.review.domain.Review;
import com.sprint.deokhugam.user.application.provided.query.UserFinder;
import com.sprint.deokhugam.user.domain.User;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentApplicationService implements CommentUseCase {

  private final CommentCommand commentCommand;
  private final CommentFinder commentFinder;

  private final ReviewFinder reviewFinder;
  private final UserFinder userFinder;

  private final CommentSearchPort commentSearchPort;

  private final NotificationCommand notificationCommand;
  private final ReviewCommand reviewCommand;
  private final ApplicationEventPublisher eventPublisher;

  @Override
  @Transactional
  public CommentDto create(CommentCreateRequest createRequest) {
    Review review = reviewFinder.getByIdOrThrow(createRequest.reviewId());
    User user = userFinder.findById(createRequest.userId());

    Comment comment = Comment.create(review, user, createRequest.content());
    Comment savedComment = commentCommand.createComment(comment);

    eventPublisher.publishEvent(
        NotifyEvent.comment(
            createRequest.reviewId(), review.getUser().getId(), user.getNickname()));

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
    decreaseCommentCountIfDeleted(comment);

    comment.softDelete();
  }

  @Override
  @Transactional
  public void hardDelete(UUID commentId, UUID userId) {
    Comment comment = getVerifiedComment(commentId, userId);
    decreaseCommentCountIfDeleted(comment);

    commentCommand.hardDelete(comment.getId());
  }

  private Comment getVerifiedComment(UUID commentId, UUID userId) {
    Comment comment = commentFinder.findById(commentId);
    if (!comment.verifyCommentId(userId)) {
      throw new BusinessException(CommentErrorCode.FORBIDDEN);
    }
    return comment;
  }

  private void decreaseCommentCountIfDeleted(Comment comment) {
    boolean isDeleted = comment.isDeleted();

    if (!isDeleted) {
      comment.getReview().decreaseCommentCount();
    } else {
      throw new BusinessException(CommonErrorCode.INTERNAL_ERROR);
    }
  }

  @Override
  @Transactional
  public void deleteCommentsByUser(UUID userId) {
    if (userId == null) {
      throw new BusinessException(CommonErrorCode.INVALID_INPUT);
    }

    List<Comment> comments = commentFinder.findAllByUserId(userId);
    if (!comments.isEmpty()) {
      Map<UUID, Long> reviewDecrementMap =
          comments.stream()
              .collect(
                  Collectors.groupingBy(
                      comment -> comment.getReview().getId(), Collectors.counting()));

      // reviewCommand.decreaseCommentCounts(reviewDecrementMap);

      commentCommand.deleteAllByUserId(userId);
    }
  }

  @Override
  @Transactional
  public void deleteCommentsByReviews(List<UUID> reviewIds) {
    if (reviewIds == null || reviewIds.isEmpty()) {
      throw new BusinessException(CommonErrorCode.INVALID_INPUT);
    }

    List<Comment> comments = commentFinder.findAllByReviewIds(reviewIds);
    if (!comments.isEmpty()) {
      commentCommand.deleteAllByReviewIds(reviewIds);
    }
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
        responses, condition.limit(), CommentDto::createdAt, CommentDto::createdAt, totalElements);
  }
}
