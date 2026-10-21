package com.sprint.deokhugam.common.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

class GlobalExceptionHandlerTest {

  // 도메인 ErrorCode 예시
  @Getter
  @RequiredArgsConstructor
  enum TestErrorCode implements ErrorCode {
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "TEST_ITEM_NOT_FOUND", "항목을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
  }

  record TestRequest(@NotBlank String name) {}

  @RestController
  static class TestController {

    @GetMapping("/business")
    void business() {
      throw new BusinessException(TestErrorCode.ITEM_NOT_FOUND);
    }

    // 외부 예외를 감싸 던지는 경우
    @GetMapping("/business-with-cause")
    void businessWithCause() {
      throw new BusinessException(
          TestErrorCode.ITEM_NOT_FOUND, "itemId=42", new IllegalStateException("외부 API 실패"));
    }

    @PostMapping("/valid")
    void valid(@RequestBody @Valid TestRequest request) {}

    @GetMapping("/header")
    void header(@RequestHeader("Deokhugam-Request-User-ID") UUID userId) {}

    @GetMapping("/params")
    void params(
        @RequestParam(defaultValue = "DESC") Sort.Direction direction,
        @RequestParam(defaultValue = "50") @Min(1) int limit) {}

    @GetMapping("/unknown")
    void unknown() {
      throw new IllegalStateException("예상하지 못한 오류");
    }
  }

  private final MockMvc mockMvc =
      MockMvcBuilders.standaloneSetup(new TestController())
          .setControllerAdvice(new GlobalExceptionHandler())
          .build();

  @Test
  @DisplayName("BusinessException은 에러 코드의 상태와 코드로 응답한다")
  void businessException() throws Exception {
    mockMvc
        .perform(get("/business"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("TEST_ITEM_NOT_FOUND"))
        .andExpect(jsonPath("$.message").value("항목을 찾을 수 없습니다."));
  }

  @Test
  @DisplayName("로그 메시지와 원인 예외를 넘겨도 응답에는 에러 코드의 메시지만 나간다")
  void businessExceptionWithCause() throws Exception {
    mockMvc
        .perform(get("/business-with-cause"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("TEST_ITEM_NOT_FOUND"))
        .andExpect(jsonPath("$.message").value("항목을 찾을 수 없습니다."));
  }

  @Test
  @DisplayName("@Valid 검증 실패는 400과 필드별 메시지로 응답한다")
  void validation() throws Exception {
    mockMvc
        .perform(post("/valid").contentType(MediaType.APPLICATION_JSON).content("{\"name\": \"\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("COMMON_INVALID_INPUT"))
        .andExpect(jsonPath("$.details.name").exists());
  }

  @Test
  @DisplayName("JSON 형식이 깨지면 400")
  void brokenJson() throws Exception {
    mockMvc
        .perform(post("/valid").contentType(MediaType.APPLICATION_JSON).content("{"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("COMMON_INVALID_INPUT"));
  }

  @Test
  @DisplayName("요청자 헤더가 없으면 400 COMMON_MISSING_HEADER")
  void missingHeader() throws Exception {
    mockMvc
        .perform(get("/header"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("COMMON_MISSING_HEADER"))
        .andExpect(jsonPath("$.details.header").value("Deokhugam-Request-User-ID"));
  }

  @Test
  @DisplayName("direction 값이 틀리면 400")
  void wrongDirection() throws Exception {
    mockMvc
        .perform(get("/params").param("direction", "abc"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("COMMON_INVALID_INPUT"));
  }

  @Test
  @DisplayName("limit이 @Min보다 작으면 400")
  void limitTooSmall() throws Exception {
    mockMvc
        .perform(get("/params").param("limit", "0"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("COMMON_INVALID_INPUT"));
  }

  @Test
  @DisplayName("없는 주소면 404")
  void notFound() throws Exception {
    mockMvc
        .perform(get("/nothing"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("COMMON_NOT_FOUND"));
  }

  @Test
  @DisplayName("지원하지 않는 메서드면 405")
  void methodNotAllowed() throws Exception {
    mockMvc
        .perform(post("/business"))
        .andExpect(status().isMethodNotAllowed())
        .andExpect(jsonPath("$.code").value("COMMON_METHOD_NOT_ALLOWED"));
  }

  @Test
  @DisplayName("알 수 없는 예외는 500과 같은 형식으로 응답한다")
  void unknownException() throws Exception {
    mockMvc
        .perform(get("/unknown"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.code").value("COMMON_INTERNAL_ERROR"))
        .andExpect(jsonPath("$.message").value("서버 내부 오류가 발생했습니다."));
  }
}
