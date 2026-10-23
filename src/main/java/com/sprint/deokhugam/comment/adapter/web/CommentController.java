package com.sprint.deokhugam.comment.adapter.web;

import com.sprint.deokhugam.comment.application.dto.CommentCreateRequest;
import com.sprint.deokhugam.comment.application.dto.CommentDto;
import com.sprint.deokhugam.comment.application.dto.CommentUpdateRequest;
import com.sprint.deokhugam.comment.application.provided.CommentUsecase;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
