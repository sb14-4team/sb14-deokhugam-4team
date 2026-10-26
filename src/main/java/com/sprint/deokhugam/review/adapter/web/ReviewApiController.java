package com.sprint.deokhugam.review.adapter.web;

import com.sprint.deokhugam.common.pagination.CursorPageResponse;
import com.sprint.deokhugam.review.application.ReviewUseCase;
import com.sprint.deokhugam.review.application.dto.req.CreateReviewCommand;
import com.sprint.deokhugam.review.application.dto.req.ReviewSearchCondition;
import com.sprint.deokhugam.review.application.dto.req.UpdateReviewRequest;
import com.sprint.deokhugam.review.application.dto.res.ReviewLikeResponse;
import com.sprint.deokhugam.review.application.dto.res.ReviewResponse;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@ControllerAdvice
@RestController
@RequestMapping("/api/reviews")
public class ReviewApiController {

  private final ReviewUseCase reviewUseCase;

  @PostMapping
  public ResponseEntity<ReviewResponse> create(@RequestBody @Valid CreateReviewCommand command) {
    ReviewResponse response = reviewUseCase.createReview(command);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/{reviewId}")
  public ResponseEntity<ReviewResponse> getReviewDetail(
      @PathVariable UUID reviewId, @RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(reviewUseCase.getReviewDetail(reviewId, requestUserId));
  }

  @PatchMapping("/{reviewId}")
  public ResponseEntity<ReviewResponse> updateReview(
      @PathVariable UUID reviewId,
      @RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId,
      @RequestBody @Valid UpdateReviewRequest request) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(
            reviewUseCase.updateReview(
                requestUserId, reviewId, request.content(), request.rating()));
  }

  @GetMapping
  public ResponseEntity<CursorPageResponse<ReviewResponse>> searchReviews(
      @RequestParam(required = false) UUID userId,
      @RequestParam(required = false) UUID bookId,
      @RequestParam(required = false) String keyword,
      @RequestParam(defaultValue = "createdAt") String orderBy,
      @RequestParam(defaultValue = "DESC") String direction,
      @RequestParam(required = false) String cursor,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          Instant after,
      @RequestParam(defaultValue = "50") int limit,
      @RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId) {

    ReviewSearchCondition condition =
        ReviewSearchCondition.toCondition(
            userId, bookId, keyword, orderBy, direction, cursor, after, limit);

    return ResponseEntity.status(HttpStatus.OK)
        .body(reviewUseCase.searchReviewsByPagination(condition, requestUserId));
  }

  @PostMapping("/{reviewId}/like")
  public ResponseEntity<ReviewLikeResponse> toggleLike(
      @PathVariable UUID reviewId, @RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(reviewUseCase.toggleLike(reviewId, requestUserId));
  }

  @DeleteMapping("/{reviewId}")
  public ResponseEntity<Void> softDeleteReview(
      @PathVariable UUID reviewId, @RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId) {
    reviewUseCase.softDeleteReview(reviewId, requestUserId);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{reviewId}/hard")
  public ResponseEntity<Void> hardDeleteReview(
      @PathVariable UUID reviewId, @RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId) {
    reviewUseCase.softDeleteReview(reviewId, requestUserId);
    return ResponseEntity.noContent().build();
  }
}
