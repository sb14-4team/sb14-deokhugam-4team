package com.sprint.deokhugam.review.adapter.web;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.deokhugam.common.exception.GlobalExceptionHandler;
import com.sprint.deokhugam.review.application.ReviewApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

// 단위테스트용
class ReviewControllerTest {
  private final ReviewApplicationService service = mock(ReviewApplicationService.class);

  private final MockMvc mockMvc =
      MockMvcBuilders.standaloneSetup(new ReviewController(service))
          .setControllerAdvice(new GlobalExceptionHandler())
          .build();

  // 평점 범위 검증
  @Test
  void 잘못된_평점_실패() throws Exception {
    mockMvc
        .perform(
            post("/api/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "userId": "00000000-0000-0000-0000-000000000001",
                      "bookId": "00000000-0000-0000-0000-000000000002",
                      "rating": 0,
                      "content": ""
                    }
                    """))
        .andExpect(jsonPath("$.details.rating").exists())
        .andExpect(status().isBadRequest());

    verifyNoInteractions(service);
  }
}
