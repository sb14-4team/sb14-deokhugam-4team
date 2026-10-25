package com.sprint.deokhugam.comment.application.required;

import com.sprint.deokhugam.comment.application.dto.CommentSearchCondition;
import com.sprint.deokhugam.comment.domain.Comment;
import java.util.List;

public interface CommentSearchPort {
  List<Comment> search(CommentSearchCondition condition);

  long count(CommentSearchCondition condition);
}
