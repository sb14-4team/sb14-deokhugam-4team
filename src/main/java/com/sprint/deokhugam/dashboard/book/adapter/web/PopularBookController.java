package com.sprint.deokhugam.dashboard.book.adapter.web;

import com.sprint.deokhugam.common.pagination.CursorPageResponse;
import com.sprint.deokhugam.dashboard.book.application.provided.query.PopularBookQuery;
import com.sprint.deokhugam.dashboard.book.application.provided.query.PopularBookSearchCondition;
import com.sprint.deokhugam.dashboard.book.application.res.PopularBookResponse;
import com.sprint.deokhugam.dashboard.book.domain.RankingPeriod;
import jakarta.validation.constraints.Min;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books")
public class PopularBookController {
  private final PopularBookQuery popularBookQuery;

  @GetMapping("/popular")
  public ResponseEntity<CursorPageResponse<PopularBookResponse>> getPopularBooks(
      @RequestParam(defaultValue = "DAILY") RankingPeriod period,
      @RequestParam(defaultValue = "ASC") Sort.Direction direction,
      @RequestParam(required = false) String cursor,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
          Instant after,
      @RequestParam(defaultValue = "50") @Min(1) int limit) {
    PopularBookSearchCondition condition =
        PopularBookSearchCondition.toCondition(period, direction, cursor, after, limit);
    CursorPageResponse<PopularBookResponse> response =
        popularBookQuery.searchPopularBooksByPagination(condition);

    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
