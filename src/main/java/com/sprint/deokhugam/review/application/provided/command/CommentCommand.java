package com.sprint.deokhugam.review.application.provided.command;

import com.sprint.deokhugam.review.application.dto.CommentCreateRequest;
import com.sprint.deokhugam.review.application.dto.CommentDto;
import com.sprint.deokhugam.review.application.dto.CommentUpdateRequest;
import java.util.UUID;

public interface CommentCommand {

  CommentDto create(CommentCreateRequest createRequest);

  CommentDto update(UUID commentId, UUID requestUserId, CommentUpdateRequest updateRequest);
}
