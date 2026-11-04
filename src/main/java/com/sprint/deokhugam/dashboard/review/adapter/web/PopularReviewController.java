package com.sprint.deokhugam.dashboard.review.adapter.web;

import com.sprint.deokhugam.common.pagination.CursorPageResponse;
import com.sprint.deokhugam.dashboard.review.application.dto.PopularReviewDto;
import com.sprint.deokhugam.dashboard.review.application.dto.PopularReviewSearchCondition;
import com.sprint.deokhugam.dashboard.review.application.provided.PopularReviewUseCase;
import com.sprint.deokhugam.dashboard.review.domain.PopularReviewPeriod;
import jakarta.validation.constraints.Min;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class PopularReviewController {

  private final PopularReviewUseCase popularReviewUseCase;

  @GetMapping("/popular")
  public ResponseEntity<CursorPageResponse<PopularReviewDto>> getPopularReviews(
      @RequestParam(defaultValue = "DAILY") PopularReviewPeriod period,
      @RequestParam(defaultValue = "ASC") Sort.Direction direction,
      @RequestParam(required = false) String cursor,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          Instant after,
      @RequestParam(defaultValue = "50") @Min(1) int limit) {
    PopularReviewSearchCondition condition =
        PopularReviewSearchCondition.toCondition(period, direction, cursor, after, limit);

    return ResponseEntity.ok(popularReviewUseCase.searchPopularReviewsByPagination(condition));
  }
}
