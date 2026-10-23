package com.sprint.deokhugam.comment.application;

import com.sprint.deokhugam.comment.application.dto.CommentUpdateRequest;
import com.sprint.deokhugam.comment.application.provided.command.CommentCommand;
import com.sprint.deokhugam.comment.application.required.CommentPersistencePort;
import com.sprint.deokhugam.comment.domain.Comment;
import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.domain.comment.exception.CommentErrorCode;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentCommandService implements CommentCommand {

  private final CommentPersistencePort commentPersistencePort;

  @Override
  @Transactional
  public Comment createComment(Comment comment) {
    return commentPersistencePort.save(comment);
  }

  @Override
  @Transactional
  public Comment updateComment(
      UUID commentId, UUID requestUserId, CommentUpdateRequest updateRequest) {
    Comment comment = commentPersistencePort.findById(commentId);

    if (!comment.verifyCommentUpdate(requestUserId)) {
      throw new BusinessException(CommentErrorCode.FORBIDDEN);
    }
    comment.update(updateRequest.Content());
    return comment;
  }
}
