package com.sprint.deokhugam.comment.adapter.persistence;

import com.sprint.deokhugam.comment.application.required.CommentPersistencePort;
import com.sprint.deokhugam.comment.domain.Comment;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentJpaPersistencePortAdapter implements CommentPersistencePort {

  private final CommentJpaRepository jpaRepository;
  private final CommentQuerydslRepository commentQuerydslRepository;

  @Override
  public Comment save(Comment entity) {
    return jpaRepository.save(entity);
  }

  @Override
  public Comment findById(UUID commentId) {
    return jpaRepository.getByIdOrThrow(commentId);
  }

  @Override
  public void delete(Comment comment) {
    jpaRepository.delete(comment);
  }

  @Override
  public void deleteCommentsByUser(UUID userId) {
    commentQuerydslRepository.deleteCommentsByUser(userId);
  }

  @Override
  public void deleteCommentsByReviews(List<UUID> reviewIds) {
    commentQuerydslRepository.deleteCommentsByReview(reviewIds);
  }

  @Override
  public List<Comment> findAllByUserId(UUID userId) {
    return commentQuerydslRepository.findByUser_Id(userId);
  }
}
