package com.sprint.deokhugam.review.application.required;

import com.sprint.deokhugam.review.domain.Comment;

public interface CommentRepository {

  Comment save(Comment entity);
}
