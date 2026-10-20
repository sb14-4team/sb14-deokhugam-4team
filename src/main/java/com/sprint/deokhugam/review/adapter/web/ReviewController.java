package com.sprint.deokhugam.review.adapter.web;

import com.sprint.deokhugam.review.application.dto.CreateReviewCommand;
import com.sprint.deokhugam.review.application.dto.ReviewResponse;
import com.sprint.deokhugam.review.application.provided.command.ReviewCommand;
import com.sprint.deokhugam.review.domain.Review;
import jakarta.validation.Valid;
import java.time.Instant;
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

  private final ReviewCommand reviewCommand;

  @PostMapping
  public ResponseEntity<ReviewResponse> create(@RequestBody @Valid CreateReviewCommand command) {
    log.info("사용자 생성 요청: {}", command);
    Review review = reviewCommand.create(command);
    // dto 변환 이후, service 쪽에 옮길 예정
    // likeByMe 임의로 false 입력
    // service에서 도서 중복 여부 검증 필요
    ReviewResponse response = ReviewResponse.from(review, false, Instant.now(), Instant.now());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }
}
