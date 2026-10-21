package com.sprint.deokhugam.review.application.required;

import com.sprint.deokhugam.review.domain.Comment;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository {

  Comment save(Comment entity);

  Optional<Comment> findById(UUID commentId);
}
