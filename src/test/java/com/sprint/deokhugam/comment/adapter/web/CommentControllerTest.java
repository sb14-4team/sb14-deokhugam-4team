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
import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.domain.comment.exception.CommentErrorCode;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
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

  private UUID reviewId;
  private UUID userId;
  private UUID commentId;

  @BeforeEach
  void setUp() {
    reviewId = UUID.randomUUID();
    userId = UUID.randomUUID();
    commentId = UUID.randomUUID();
  }

  @Test
  @DisplayName("댓글 등록 성공 테스트")
  void createComment_success() throws Exception {
    // Given
    CommentCreateRequest createRequest =
        new CommentCreateRequest(reviewId, userId, "testCreateContent");

    CommentDto responseDto =
        CommentDto.to(
            UUID.randomUUID(),
            reviewId,
            userId,
            "testNickname",
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
        .andExpect(jsonPath("$.content").value("testCreateContent"));
  }

  @Test
  @DisplayName("댓글 등록 실패 - 빈 내용 입력 시 400 에러")
  void createComment_fail_blankContent() throws Exception {
    // Given
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
  void updateComment_success() throws Exception {
    // Given
    CommentUpdateRequest updateRequest = new CommentUpdateRequest("testUpdatedContent");

    CommentDto commentDto =
        CommentDto.to(
            commentId,
            reviewId,
            userId,
            "testNickname",
            updateRequest.Content(),
            Instant.now(),
            Instant.now());

    given(commentCommand.update(eq(commentId), eq(userId), any(CommentUpdateRequest.class)))
        .willReturn(commentDto);

    // When & Then
    mockMvc
        .perform(
            patch("/api/comments/{commentId}", commentId)
                .param("requestUserId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").value("testUpdatedContent"));
  }

  @Test
  @DisplayName("댓글 수정 실패 - 작성자가 아닌 타인이 요청할 경우 예외 발생")
  void updateComment_forbidden() throws Exception {
    // Given
    CommentUpdateRequest updateRequest = new CommentUpdateRequest("testUpdatedContent");

    given(commentCommand.update(eq(commentId), eq(userId), any(CommentUpdateRequest.class)))
        .willThrow(new BusinessException(CommentErrorCode.FORBIDDEN));

    // When & Then
    mockMvc
        .perform(
            patch("/api/comments/{commentId}", commentId)
                .param("requestUserId", userId.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isForbidden());
  }
}
