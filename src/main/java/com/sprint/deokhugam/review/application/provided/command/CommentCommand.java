package com.sprint.deokhugam.review.application.provided.command;

import com.sprint.deokhugam.review.application.dto.CommentCreateRequest;
import com.sprint.deokhugam.review.application.dto.CommentDto;

public interface CommentCommand {

  CommentDto create(CommentCreateRequest createRequest);
}
