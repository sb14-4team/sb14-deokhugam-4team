package com.sprint.deokhugam.dashboard.review.adapter.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.deokhugam.common.pagination.CursorPageResponse;
import com.sprint.deokhugam.dashboard.review.application.dto.PopularReviewDto;
import com.sprint.deokhugam.dashboard.review.application.dto.PopularReviewSearchCondition;
import com.sprint.deokhugam.dashboard.review.application.provided.PopularReviewUseCase;
import com.sprint.deokhugam.dashboard.review.domain.PopularReviewPeriod;
import com.sprint.deokhugam.review.adapter.web.ReviewApiController;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
    controllers = PopularReviewController.class,
    excludeFilters =
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            // /api/reviews를 쓰기 때문에 같이 뜨지 않도록 제외
            classes = ReviewApiController.class))
public class PopularReviewControllerTest {

  private static final Instant AFTER = Instant.parse("2026-11-03T15:00:00Z");

  @Autowired private MockMvc mockMvc;
  @MockitoBean private PopularReviewUseCase popularReviewUseCase;

  @Test
  @DisplayName("인기 리뷰 조회 성공")
  void getPopularReviews_success_withParams() throws Exception {
    // Given
    given(popularReviewUseCase.searchPopularReviewsByPagination(any()))
        .willReturn(
            CursorPageResponse.of(
                List.of(), 10, PopularReviewDto::rank, PopularReviewDto::createdAt, 1L));

    // When & Then
    mockMvc
        .perform(
            get("/api/reviews/popular")
                .param("period", "WEEKLY")
                .param("direction", "DESC")
                .param("cursor", "3")
                .param("after", AFTER.toString())
                .param("limit", "10"))
        .andExpect(status().isOk());

    PopularReviewSearchCondition expected =
        new PopularReviewSearchCondition(
            PopularReviewPeriod.WEEKLY, Sort.Direction.DESC, 3L, AFTER, 10);
    verify(popularReviewUseCase).searchPopularReviewsByPagination(expected);
  }

  @Test
  @DisplayName("인기 리뷰 조회 성공 - 기본값(DAILY, ASC, 50)으로 조회")
  void getPopularReviews_success_defaultParams() throws Exception {
    // Given
    PopularReviewDto dto =
        new PopularReviewDto(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            "testTitle",
            null,
            UUID.randomUUID(),
            "testNickname",
            "testReviewContent",
            4.0,
            PopularReviewPeriod.DAILY,
            AFTER,
            1L,
            1.7,
            1L,
            2L);
    given(popularReviewUseCase.searchPopularReviewsByPagination(any()))
        .willReturn(
            CursorPageResponse.of(
                List.of(dto), 50, PopularReviewDto::rank, PopularReviewDto::createdAt, 1L));

    // When & Then
    mockMvc
        .perform(get("/api/reviews/popular"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].rank").value(1))
        .andExpect(jsonPath("$.content[0].score").value(1.7))
        .andExpect(jsonPath("$.hasNext").value(false));

    PopularReviewSearchCondition expected =
        new PopularReviewSearchCondition(
            PopularReviewPeriod.DAILY, Sort.Direction.ASC, null, null, 50);
    verify(popularReviewUseCase).searchPopularReviewsByPagination(expected);
  }

  @Test
  @DisplayName("인기 리뷰 조회 실패 - cursor만 보내고 after를 뺀 경우")
  void getPopularReviews_fail_cursorWithoutAfter() throws Exception {
    mockMvc
        .perform(get("/api/reviews/popular").param("cursor", "3"))
        .andExpect(status().isBadRequest());

    // 서비스가 한 번도 호출되지 않았는지 확인
    verify(popularReviewUseCase, never()).searchPopularReviewsByPagination(any());
  }

  @Test
  @DisplayName("인기 리뷰 조회 실패 - cursor가 숫자가 아닌 경우")
  void getPopularReviews_fail_cursorNotNumber() throws Exception {
    mockMvc
        .perform(
            get("/api/reviews/popular").param("cursor", "abc").param("after", AFTER.toString()))
        .andExpect(status().isBadRequest());

    verify(popularReviewUseCase, never()).searchPopularReviewsByPagination(any());
  }

  @Test
  @DisplayName("인기 리뷰 조회 실패 - 없는 기간 값인 경우")
  void getPopularReviews_fail_invalidPeriod() throws Exception {
    mockMvc
        .perform(get("/api/reviews/popular").param("period", "YEARLY"))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("인기 리뷰 조회 실패 - limit가 1보다 작은 경우")
  void getPopularReviews_fail_limitLessThanOne() throws Exception {
    mockMvc
        .perform(get("/api/reviews/popular").param("limit", "0"))
        .andExpect(status().isBadRequest());
  }
}
