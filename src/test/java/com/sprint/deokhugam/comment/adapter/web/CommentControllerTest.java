package com.sprint.deokhugam.comment.adapter.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.deokhugam.comment.application.dto.CommentCreateRequest;
import com.sprint.deokhugam.comment.application.dto.CommentDto;
import com.sprint.deokhugam.comment.application.dto.CommentUpdateRequest;
import com.sprint.deokhugam.comment.application.provided.command.CommentCommand;
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
  @MockitoBean private CommentCommand commentCommand;

  @Test
  @DisplayName("댓글 등록 성공 테스트")
  void createComment_success() throws Exception {
    // Given
    UUID reviewId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    CommentCreateRequest createRequest =
        new CommentCreateRequest(reviewId, userId, "test_create_content");

    CommentDto responseDto =
        CommentDto.to(
            UUID.randomUUID(),
            reviewId,
            userId,
            "test_nickname",
            createRequest.content(),
            Instant.now(),
            Instant.now());

    given(commentCommand.create(any(CommentCreateRequest.class))).willReturn(responseDto);

    // When & Then
    mockMvc
        .perform(
            post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(createRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.content").value("test_create_content"));
  }

  @Test
  @DisplayName("댓글 등록 실패 - 빈 내용 입력 시 400 에러")
  void createComment_fail_blankContent() throws Exception {
    // Given
    UUID reviewId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    CommentCreateRequest invalidRequest = new CommentCreateRequest(reviewId, userId, "");

    // When & Then
    mockMvc
        .perform(
            post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("댓글 수정 성공 테스트")
  void updateComment_Success() throws Exception {
    // Given
    UUID commentId = UUID.randomUUID();
    UUID requestUserId = UUID.randomUUID();
    UUID reviewId = UUID.randomUUID();

    CommentUpdateRequest updateRequest = new CommentUpdateRequest("test_updated_content");

    CommentDto commentDto =
        CommentDto.to(
            commentId,
            reviewId,
            requestUserId,
            "test_nickname",
            updateRequest.Content(),
            Instant.now(),
            Instant.now());

    given(commentCommand.update(eq(commentId), eq(requestUserId), any(CommentUpdateRequest.class)))
        .willReturn(commentDto);

    // When & Then
    mockMvc
        .perform(
            patch("/api/comments/{commentId}", commentId)
                .param("requestUserId", requestUserId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").value("test_updated_content"));
  }
}
