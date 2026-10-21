package com.sprint.deokhugam.review.adapter.web;

import com.sprint.deokhugam.review.application.ReviewApplicationService;
import com.sprint.deokhugam.review.application.dto.req.CreateReviewCommand;
import com.sprint.deokhugam.review.application.dto.res.ReviewResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
    log.info("리뷰 생성 요청: {}", command);

    ReviewResponse response = reviewApplicationService.createReview(command);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{reviewId}")
  public ResponseEntity<ReviewResponse> getReviewDetail(
      @PathVariable UUID reviewId, @RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(reviewApplicationService.getReviewDetail(reviewId, requestUserId));
  }
}
