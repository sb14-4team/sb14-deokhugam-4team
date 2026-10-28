package com.sprint.deokhugam.comment.application;

import com.sprint.deokhugam.comment.application.provided.query.CommentFinder;
import com.sprint.deokhugam.comment.application.required.CommentPersistencePort;
import com.sprint.deokhugam.comment.domain.Comment;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentQueryService implements CommentFinder {

  private final CommentPersistencePort commentPersistencePort;

  @Override
  public Comment findById(UUID commentId) {
    return commentPersistencePort.findById(commentId);
  }

  @Override
  public List<Comment> findAllByUserId(UUID userId) {
    return commentPersistencePort.findAllByUserId(userId);
  }

  @Override
  public List<Comment> findAllByReviewIds(List<UUID> reviewIds) {
    return commentPersistencePort.findAllByReviewIds(reviewIds);
  }
}
