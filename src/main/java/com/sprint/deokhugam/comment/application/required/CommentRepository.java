package com.sprint.deokhugam.comment.application.required;

import com.sprint.deokhugam.comment.domain.Comment;
import java.util.Optional;
import java.util.UUID;

public interface CommentRepository {

  Comment save(Comment entity);

  Optional<Comment> findById(UUID commentId);
}
