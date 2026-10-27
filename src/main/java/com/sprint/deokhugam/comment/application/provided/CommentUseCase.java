package com.sprint.deokhugam.comment.application.provided;

import com.sprint.deokhugam.comment.application.dto.CommentCreateRequest;
import com.sprint.deokhugam.comment.application.dto.CommentDto;
import com.sprint.deokhugam.comment.application.dto.CommentSearchCondition;
import com.sprint.deokhugam.comment.application.dto.CommentUpdateRequest;
import com.sprint.deokhugam.common.pagination.CursorPageResponse;
import java.util.UUID;

// API Swagger를 기반
public interface CommentUseCase {

  CommentDto create(CommentCreateRequest createRequest);

  CommentDto update(UUID commentId, UUID requestUserId, CommentUpdateRequest updateRequest);

  void softDelete(UUID commentId, UUID userId);

  void hardDelete(UUID commentId, UUID userId);

  void deleteCommentsByUser(UUID userId);

  void deleteCommentsByReview(UUID reviewId);

  CursorPageResponse<CommentDto> searchCommentsByPagination(CommentSearchCondition condition);
}
