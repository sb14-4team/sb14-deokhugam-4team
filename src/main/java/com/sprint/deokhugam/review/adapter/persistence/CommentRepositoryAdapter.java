package com.sprint.deokhugam.review.adapter.persistence;

import com.sprint.deokhugam.review.application.required.CommentRepository;
import com.sprint.deokhugam.review.domain.Comment;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryAdapter implements CommentRepository {

  private final CommentJpaRepository jpaRepository;

  @Override
  public Comment save(Comment entity) {
    return jpaRepository.save(entity);
  }

  @Override
  public Optional<Comment> findById(UUID commentId) {
    return jpaRepository.findById(commentId);
  }
}
