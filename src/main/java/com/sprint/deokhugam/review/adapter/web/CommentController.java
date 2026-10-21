package com.sprint.deokhugam.review.adapter.web;

import com.sprint.deokhugam.review.application.dto.CommentCreateRequest;
import com.sprint.deokhugam.review.application.dto.CommentDto;
import com.sprint.deokhugam.review.application.dto.CommentUpdateRequest;
import com.sprint.deokhugam.review.application.provided.command.CommentCommand;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

  private final CommentCommand commentCommand;

  @PostMapping
  public ResponseEntity<CommentDto> create(@RequestBody @Valid CommentCreateRequest createRequest) {
    CommentDto commentDto = commentCommand.create(createRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(commentDto);
  }

  @PatchMapping("{commentId}")
  public ResponseEntity<CommentDto> update(
      @PathVariable UUID commentId,
      @RequestParam UUID requestUserId,
      @RequestBody @Valid CommentUpdateRequest updateRequest) {
    CommentDto commentDto = commentCommand.update(commentId, requestUserId, updateRequest);
    return ResponseEntity.status(HttpStatus.OK).body(commentDto);
  }
}
