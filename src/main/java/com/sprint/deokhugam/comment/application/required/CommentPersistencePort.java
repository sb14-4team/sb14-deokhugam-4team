package com.sprint.deokhugam.comment.application.required;

import com.sprint.deokhugam.comment.domain.Comment;
import java.util.List;
import java.util.UUID;

public interface CommentPersistencePort {

  Comment save(Comment entity);

  Comment findById(UUID commentId);

  List<Comment> findAllByUserIds(List<UUID> userIds);

  void delete(Comment comment);

  void deleteCommentsByUsers(List<UUID> userIds);
}
