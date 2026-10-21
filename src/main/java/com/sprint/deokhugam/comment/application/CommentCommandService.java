package com.sprint.deokhugam.comment.application;

import com.sprint.deokhugam.comment.application.dto.CommentCreateRequest;
import com.sprint.deokhugam.comment.application.dto.CommentDto;
import com.sprint.deokhugam.comment.application.dto.CommentUpdateRequest;
import com.sprint.deokhugam.comment.application.provided.command.CommentCommand;
import com.sprint.deokhugam.comment.application.required.CommentRepository;
import com.sprint.deokhugam.comment.domain.Comment;
import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.domain.comment.exception.CommentErrorCode;
import com.sprint.deokhugam.domain.user.exception.UserErrorCode;
import com.sprint.deokhugam.user.application.required.UserRepository;
import com.sprint.deokhugam.user.domain.User;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentCommandService implements CommentCommand {

  private final CommentRepository commentRepository;
  // private final ReviewRepository reviewRepository;
  private final UserRepository userRepository;

  @Transactional
  @Override
  public CommentDto create(CommentCreateRequest createRequest) {
    // Review review = reviewRepository.findByReviewId(createRequest.reviewId());
    User user =
        userRepository
            .findById(createRequest.userId())
            .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));

    // review에 임의로 null 대입
    Comment comment = Comment.create(null, user, createRequest.content());
    commentRepository.save(comment);

    return CommentDto.to(
        comment.getId(),
        comment.getReview().getId(),
        comment.getUser().getId(),
        comment.getUser().getNickname(),
        comment.getContent(),
        Instant.now(),
        Instant.now());
  }

  @Transactional
  @Override
  public CommentDto update(UUID commentId, UUID requestUserId, CommentUpdateRequest updateRequest) {
    Comment comment =
        commentRepository
            .findById(commentId)
            .orElseThrow(() -> new BusinessException(CommentErrorCode.NOT_FOUND));
    if (!comment.getUser().getId().equals(requestUserId)) {
      throw new BusinessException(CommentErrorCode.FORBIDDEN);
    }
    comment.update(updateRequest.Content());

    return CommentDto.to(
        commentId,
        comment.getReview().getId(),
        comment.getUser().getId(),
        comment.getUser().getNickname(),
        comment.getContent(),
        comment.getCreatedAt(),
        Instant.now());
  }
}
