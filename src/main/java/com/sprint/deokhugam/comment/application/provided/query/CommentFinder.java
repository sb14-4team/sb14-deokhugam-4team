package com.sprint.deokhugam.comment.application.provided.query;

import com.sprint.deokhugam.comment.domain.Comment;
import java.util.List;
import java.util.UUID;

// R 기능
public interface CommentFinder {

  Comment findById(UUID commentId);

  List<Comment> findAllByUserIds(List<UUID> userIds);
}
