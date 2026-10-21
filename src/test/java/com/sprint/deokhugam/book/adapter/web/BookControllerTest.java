package com.sprint.deokhugam.book.adapter.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.deokhugam.book.FakeBookPersistencePort;
import com.sprint.deokhugam.book.application.BookCommandService;
import com.sprint.deokhugam.common.exception.GlobalExceptionHandler;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class BookControllerTest {

  private final MockMvc mockMvc =
      MockMvcBuilders.standaloneSetup(
              new BookController(new BookCommandService(new FakeBookPersistencePort())))
          .setControllerAdvice(new GlobalExceptionHandler())
          .build();

  private MockMultipartFile bookData(String json) {
    return new MockMultipartFile(
        "bookData", "", MediaType.APPLICATION_JSON_VALUE, json.getBytes(StandardCharsets.UTF_8));
  }

  private static final String VALID_JSON =
      """
      {
        "title": "클린 코드",
        "author": "로버트 C. 마틴",
        "description": "소개",
        "publisher": "인사이트",
        "publishedDate": "2013-12-24",
        "isbn": "9788966260959"
      }
      """;

  @Test
  @DisplayName("도서를 등록하면 201과 도서 정보를 돌려준다")
  void create() throws Exception {
    MockMultipartFile thumbnail =
        new MockMultipartFile("thumbnailImage", "cover.png", "image/png", new byte[] {1, 2, 3});

    mockMvc
        .perform(multipart("/api/books").file(bookData(VALID_JSON)).file(thumbnail))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.title").value("클린 코드"))
        .andExpect(jsonPath("$.publishedDate").value("2013-12-24"))
        .andExpect(jsonPath("$.isbn").value("9788966260959"))
        .andExpect(jsonPath("$.reviewCount").value(0))
        .andExpect(jsonPath("$.rating").value(0.0));
  }

  @Test
  @DisplayName("필수 값이 비어 있으면 400과 필드별 메시지를 돌려준다")
  void createWithBlankTitle() throws Exception {
    String json = VALID_JSON.replace("\"클린 코드\"", "\"\"");

    mockMvc
        .perform(multipart("/api/books").file(bookData(json)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("COMMON_INVALID_INPUT"))
        .andExpect(jsonPath("$.details.title").value("제목은 필수입니다."));
  }

  @Test
  @DisplayName("제목이 너무 길면 400을 돌려준다")
  void createWithTooLongTitle() throws Exception {
    String json = VALID_JSON.replace("클린 코드", "가".repeat(256));

    mockMvc
        .perform(multipart("/api/books").file(bookData(json)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("COMMON_INVALID_INPUT"));
  }

  @Test
  @DisplayName("이미 등록된 ISBN이면 409를 돌려준다")
  void createWithDuplicateIsbn() throws Exception {
    mockMvc.perform(multipart("/api/books").file(bookData(VALID_JSON)));

    mockMvc
        .perform(multipart("/api/books").file(bookData(VALID_JSON)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("BOOK_DUPLICATE_ISBN"));
  }
}
