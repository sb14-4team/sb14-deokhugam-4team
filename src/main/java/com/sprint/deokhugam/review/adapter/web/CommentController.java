package com.sprint.deokhugam.review.adapter.web;

import com.sprint.deokhugam.review.application.dto.CommentCreateRequest;
import com.sprint.deokhugam.review.application.dto.CommentDto;
import com.sprint.deokhugam.review.application.provided.command.CommentCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

  private final CommentCommand commentCommand;

  @PostMapping
  public ResponseEntity<CommentDto> create(@RequestBody @Valid CommentCreateRequest createRequest) {
    log.info("댓글 생성 요청: {}", createRequest);
    CommentDto commentDto = commentCommand.create(createRequest);
    log.debug("리뷰 생성 응답: {}", commentDto);
    return ResponseEntity.status(HttpStatus.CREATED).body(commentDto);
  }
}
