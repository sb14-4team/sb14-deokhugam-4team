package com.sprint.deokhugam.comment.application.provided.command;

import com.sprint.deokhugam.comment.application.dto.CommentCreateRequest;
import com.sprint.deokhugam.comment.application.dto.CommentDto;
import com.sprint.deokhugam.comment.application.dto.CommentUpdateRequest;
import java.util.UUID;

public interface CommentCommand {

  CommentDto create(CommentCreateRequest createRequest);

  CommentDto update(UUID commentId, UUID requestUserId, CommentUpdateRequest updateRequest);
}
