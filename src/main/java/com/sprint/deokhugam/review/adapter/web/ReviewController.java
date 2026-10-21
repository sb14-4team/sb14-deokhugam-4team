package com.sprint.deokhugam.review.adapter.web;

import com.sprint.deokhugam.review.application.ReviewApplicationService;
import com.sprint.deokhugam.review.application.dto.ReviewResponse;
import com.sprint.deokhugam.review.application.dto.req.CreateReviewCommand;
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
@RequestMapping("/api/reviews")
public class ReviewController {

  private final ReviewApplicationService reviewApplicationService;

  @PostMapping
  public ResponseEntity<ReviewResponse> create(@RequestBody @Valid CreateReviewCommand command) {
    log.info("사용자 생성 요청: {}", command);

    ReviewResponse response = reviewApplicationService.createReview(command);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}
