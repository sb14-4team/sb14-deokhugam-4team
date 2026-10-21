package com.sprint.deokhugam.comment.adapter.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.deokhugam.comment.application.CommentCommandService;
import com.sprint.deokhugam.comment.application.dto.CommentCreateRequest;
import com.sprint.deokhugam.comment.application.dto.CommentDto;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.json.JsonMapper;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private JsonMapper jsonMapper;
  @MockitoBean private CommentCommandService commandService;

  @Test
  @DisplayName("댓글 컨트롤러 등록 정상 요청")
  void createComment_success() throws Exception {
    // Given

    UUID reviewId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    CommentCreateRequest createRequest = new CommentCreateRequest(reviewId, userId, "test_content");

    CommentDto responseDto =
        CommentDto.to(
            UUID.randomUUID(),
            reviewId,
            userId,
            "test_nickname",
            "test_content",
            Instant.now(),
            Instant.now());

    given(commandService.create(any(CommentCreateRequest.class))).willReturn(responseDto);

    // When & Then
    mockMvc
        .perform(
            post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(createRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.content").value("test_content"));
  }
}
