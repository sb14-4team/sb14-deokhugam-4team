package com.sprint.deokhugam.review.adapter.persistence;

import com.sprint.deokhugam.review.application.required.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryAdapter implements CommentRepository {

  private final CommentJpaRepository commentJpaRepository;
}
