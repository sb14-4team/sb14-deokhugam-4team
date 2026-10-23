package com.sprint.deokhugam.comment.adapter.persistence;

import com.sprint.deokhugam.comment.application.required.CommentPersistencePort;
import com.sprint.deokhugam.comment.domain.Comment;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentJpaPersistencePortAdapter implements CommentPersistencePort {

  private final CommentJpaRepository jpaRepository;

  @Override
  public Comment save(Comment entity) {
    return jpaRepository.save(entity);
  }

  @Override
  public Comment findById(UUID commentId) {
    return jpaRepository.getByIdOrThrow(commentId);
  }
}
