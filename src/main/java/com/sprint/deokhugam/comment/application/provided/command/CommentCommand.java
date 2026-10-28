package com.sprint.deokhugam.comment.application.provided.command;

import com.sprint.deokhugam.comment.application.dto.CommentUpdateRequest;
import com.sprint.deokhugam.comment.domain.Comment;
import java.util.List;
import java.util.UUID;

// CUD 기능
public interface CommentCommand {

  Comment createComment(Comment comment);

  Comment updateComment(UUID commentId, UUID requestUserId, CommentUpdateRequest updateRequest);

  void hardDelete(UUID commentId);

  void deleteAllByUserId(UUID userId);

  void deleteAllByReviewIds(List<UUID> reviewIds);
}
