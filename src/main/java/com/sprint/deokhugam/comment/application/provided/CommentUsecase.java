package com.sprint.deokhugam.comment.application.provided;

import com.sprint.deokhugam.comment.application.dto.CommentCreateRequest;
import com.sprint.deokhugam.comment.application.dto.CommentDto;
import com.sprint.deokhugam.comment.application.dto.CommentUpdateRequest;
import java.util.UUID;

// API Swagger를 기반
public interface CommentUsecase {

  CommentDto create(CommentCreateRequest createRequest);

  CommentDto update(UUID commentId, UUID requestUserId, CommentUpdateRequest updateRequest);
}
