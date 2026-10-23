package com.sprint.deokhugam.comment.adapter.web;

import com.sprint.deokhugam.comment.application.dto.CommentCreateRequest;
import com.sprint.deokhugam.comment.application.dto.CommentDto;
import com.sprint.deokhugam.comment.application.dto.CommentUpdateRequest;
import com.sprint.deokhugam.comment.application.provided.CommentUsecase;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    CommentDto createComment = commentApplicationService.create(createRequest);
    return ResponseEntity.ok(createComment);
  }

  @PatchMapping("{commentId}")
  public ResponseEntity<CommentDto> update(
      @PathVariable UUID commentId,
      @RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId,
      @RequestBody @Valid CommentUpdateRequest updateRequest) {
    CommentDto updatedComment =
        commentApplicationService.update(commentId, requestUserId, updateRequest);
    return ResponseEntity.ok(updatedComment);
  }

  @DeleteMapping("{commentId}")
  public ResponseEntity<Void> delete(
      @PathVariable UUID commentId, @RequestHeader("Deokhugam-Request-User-ID") UUID userId) {
    commentApplicationService.delete(commentId, userId);
    return ResponseEntity.noContent().build();
  }
}
