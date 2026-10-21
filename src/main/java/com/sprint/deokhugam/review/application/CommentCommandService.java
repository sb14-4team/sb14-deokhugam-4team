package com.sprint.deokhugam.review.application;

import com.sprint.deokhugam.review.application.dto.CommentCreateRequest;
import com.sprint.deokhugam.review.application.dto.CommentDto;
import com.sprint.deokhugam.review.application.provided.command.CommentCommand;
import com.sprint.deokhugam.review.application.required.CommentRepository;
import com.sprint.deokhugam.review.domain.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentCommandService implements CommentCommand {

  private final CommentRepository commentRepository;

  // private final ReviewRepository reviewRepository;
  // private final UserRepository userRepository;

  @Override
  public CommentDto create(CommentCreateRequest createRequest) {
    // Review review = reviewRepository.findByReviewId(createRequest.reviewId());
    // User user = userRepository.findByUserId(createRequest.userId());

    // review랑 user에 임의로 null 대입
    Comment comment = Comment.create(null, null, createRequest.content());

    return CommentDto.to(
        comment.getId(),
        comment.getReview().getId(),
        comment.getUser().getId(),

        // comment.getUser().getNickname(),
        null,
        comment.getContent(),
        comment.getCreatedAt(),
        comment.getUpdated());
  }
}
