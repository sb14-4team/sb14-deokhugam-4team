package com.sprint.deokhugam.comment.application.required;

import com.sprint.deokhugam.comment.domain.Comment;
import java.util.List;
import java.util.UUID;

public interface CommentPersistencePort {

  Comment save(Comment entity);

  Comment findById(UUID commentId);

  List<Comment> findAllByUserId(UUID userId);

  List<Comment> findAllByReviewId(UUID reviewId);

  void delete(Comment comment);

  void deleteCommentsByUser(UUID userId);

  void deleteCommentsByReview(UUID reviewId);
}
