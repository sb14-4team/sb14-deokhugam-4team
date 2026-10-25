package com.sprint.deokhugam.comment.adapter.web;

import com.sprint.deokhugam.comment.application.dto.CommentCreateRequest;
import com.sprint.deokhugam.comment.application.dto.CommentDto;
import com.sprint.deokhugam.comment.application.dto.CommentSearchCondition;
import com.sprint.deokhugam.comment.application.dto.CommentUpdateRequest;
import com.sprint.deokhugam.comment.application.provided.CommentUsecase;
import com.sprint.deokhugam.common.pagination.CursorPageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

  private final CommentUsecase commentApplicationService;

  @PostMapping
  public ResponseEntity<CommentDto> create(@RequestBody @Valid CommentCreateRequest createRequest) {
    CommentDto commentDto = commentApplicationService.create(createRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(commentDto);
  }

  @PatchMapping("{commentId}")
  public ResponseEntity<CommentDto> update(
      @PathVariable UUID commentId,
      @RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId,
      @RequestBody @Valid CommentUpdateRequest updateRequest) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(commentApplicationService.update(commentId, requestUserId, updateRequest));
  }

  @GetMapping
  public ResponseEntity<CursorPageResponse<CommentDto>> search(
      @RequestParam UUID reviewId,
      @RequestParam(defaultValue = "DESC") Sort.Direction direction,
      @RequestParam(required = false) String cursor,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          Instant after,
      @RequestParam(defaultValue = "50") @Min(1) int limit) {
    CommentSearchCondition condition =
        CommentSearchCondition.toCondition(reviewId, direction, cursor, after, limit);

    return ResponseEntity.status(HttpStatus.OK)
        .body(commentApplicationService.searchCommentsByPagination(condition));
  }
}
