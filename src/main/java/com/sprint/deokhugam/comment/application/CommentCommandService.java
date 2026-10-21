package com.sprint.deokhugam.comment.application;

import com.sprint.deokhugam.comment.application.dto.CommentCreateRequest;
import com.sprint.deokhugam.comment.application.dto.CommentDto;
import com.sprint.deokhugam.comment.application.dto.CommentUpdateRequest;
import com.sprint.deokhugam.comment.application.provided.command.CommentCommand;
import com.sprint.deokhugam.comment.application.required.CommentRepository;
import com.sprint.deokhugam.comment.domain.Comment;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentCommandService implements CommentCommand {

  private final CommentRepository commentRepository;

  // private final ReviewRepository reviewRepository;
  // private final UserRepository userRepository;

  @Transactional
  @Override
  public CommentDto create(CommentCreateRequest createRequest) {
    // Review review = reviewRepository.findByReviewId(createRequest.reviewId());
    // User user = userRepository.findByUserId(createRequest.userId());

    // review랑 user에 임의로 null 대입
    Comment comment = Comment.create(null, null, createRequest.content());
    commentRepository.save(comment);

    return CommentDto.to(
        comment.getId(),
        comment.getReview().getId(),
        comment.getUser().getId(),

        // comment.getUser().getNickname(),
        null,
        comment.getContent(),
        Instant.now(),
        Instant.now());
  }

  @Transactional
  @Override
  public CommentDto update(UUID commentId, UUID requestUserId, CommentUpdateRequest updateRequest) {
    Comment comment =
        commentRepository.findById(commentId).orElseThrow(() -> new IllegalArgumentException());
    if (!comment.getUser().getId().equals(requestUserId)) {
      throw new NoSuchElementException();
    }
    comment.update(updateRequest.Content());

    return CommentDto.to(
        commentId,
        comment.getReview().getId(),
        comment.getUser().getId(),

        // comment.getUser().getNickname(),
        null,
        comment.getContent(),
        comment.getCreatedAt(),
        Instant.now());
  }
}
