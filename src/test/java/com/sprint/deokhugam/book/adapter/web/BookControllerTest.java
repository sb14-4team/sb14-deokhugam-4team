package com.sprint.deokhugam.book.adapter.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.deokhugam.book.FakeBookPersistencePort;
import com.sprint.deokhugam.book.application.BookCommandService;
import com.sprint.deokhugam.book.application.BookQueryService;
import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.common.exception.GlobalExceptionHandler;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class BookControllerTest {

  private final FakeBookPersistencePort bookPersistencePort = new FakeBookPersistencePort();

  private final MockMvc mockMvc =
      MockMvcBuilders.standaloneSetup(
              new BookController(
                  new BookCommandService(bookPersistencePort),
                  new BookQueryService(bookPersistencePort)))
          .setControllerAdvice(new GlobalExceptionHandler())
          .build();

  private MockMultipartFile bookData(String json) {
    return new MockMultipartFile(
        "bookData", "", MediaType.APPLICATION_JSON_VALUE, json.getBytes(StandardCharsets.UTF_8));
  }

  private Book savedBook() {
    return bookPersistencePort.save(
        Book.create(
            "클린 코드", "로버트 C. 마틴", "소개", "인사이트", LocalDate.of(2013, 12, 24), "9788966260959", null));
  }

  private Book savedBook(String title) {
    return bookPersistencePort.save(
        Book.create(title, "저자", "소개", "출판사", LocalDate.of(2020, 1, 1), null, null));
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

  // ===== (1) POST /api/books : create() =====
  // (1-1)
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

  // (1-2)
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

  // (1-3)
  @Test
  @DisplayName("제목이 너무 길면 400을 돌려준다")
  void createWithTooLongTitle() throws Exception {
    String json = VALID_JSON.replace("클린 코드", "가".repeat(256));

    mockMvc
        .perform(multipart("/api/books").file(bookData(json)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("COMMON_INVALID_INPUT"));
  }

  // (1-4)
  @Test
  @DisplayName("이미 등록된 ISBN이면 409를 돌려준다")
  void createWithDuplicateIsbn() throws Exception {
    mockMvc.perform(multipart("/api/books").file(bookData(VALID_JSON)));

    mockMvc
        .perform(multipart("/api/books").file(bookData(VALID_JSON)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("BOOK_DUPLICATE_ISBN"));
  }

  // ===== (2) GET /api/books/{bookId} : get() =====
  // (2-1)
  @Test
  @DisplayName("도서 id로 조회하면 200과 도서 정보를 돌려준다")
  void getBook() throws Exception {
    Book saved = savedBook();

    mockMvc
        .perform(get("/api/books/{bookId}", saved.getId()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(saved.getId().toString()))
        .andExpect(jsonPath("$.title").value("클린 코드"))
        .andExpect(jsonPath("$.author").value("로버트 C. 마틴"))
        .andExpect(jsonPath("$.publishedDate").value("2013-12-24"))
        .andExpect(jsonPath("$.isbn").value("9788966260959"))
        .andExpect(jsonPath("$.reviewCount").value(0))
        .andExpect(jsonPath("$.rating").value(0.0));
  }

  // (2-2)
  @Test
  @DisplayName("없는 도서 id로 조회하면 404를 돌려준다")
  void getBookNotFound() throws Exception {
    mockMvc
        .perform(get("/api/books/{bookId}", UUID.randomUUID()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("BOOK_NOT_FOUND"));
  }

  // (2-3)
  @Test
  @DisplayName("논리 삭제된 도서를 조회하면 404를 돌려준다")
  void getDeletedBook() throws Exception {
    Book saved = savedBook();
    saved.softDelete();

    mockMvc
        .perform(get("/api/books/{bookId}", saved.getId()))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value("BOOK_NOT_FOUND"));
  }

  // (2-4)
  @Test
  @DisplayName("도서 id가 UUID 형식이 아니면 400을 돌려준다")
  void getBookWithInvalidId() throws Exception {
    mockMvc
        .perform(get("/api/books/{bookId}", "not-a-uuid"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("COMMON_INVALID_INPUT"));
  }

  // ===== (3) GET /api/books : search() =====
  // (3-1)
  @Test
  @DisplayName("파라미터 없이 조회하면 제목 내림차순으로 200과 커서 페이지를 돌려준다")
  void search() throws Exception {
    savedBook("A");
    savedBook("B");
    savedBook("C");

    mockMvc
        .perform(get("/api/books").param("limit", "2"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.content[0].title").value("C"))
        .andExpect(jsonPath("$.content[1].title").value("B"))
        .andExpect(jsonPath("$.size").value(2))
        .andExpect(jsonPath("$.totalElements").value(3))
        .andExpect(jsonPath("$.hasNext").value(true))
        .andExpect(jsonPath("$.nextCursor").value("B"))
        .andExpect(jsonPath("$.nextAfter").value("2026-01-01T00:00:01Z"));
  }

  // (3-2)
  @Test
  @DisplayName("cursor와 after를 넘기면 그 다음 도서부터 돌려준다")
  void searchNextPage() throws Exception {
    savedBook("A");
    savedBook("B");
    savedBook("C");

    mockMvc
        .perform(
            get("/api/books")
                .param("limit", "2")
                .param("cursor", "B")
                .param("after", "2026-01-01T00:00:01Z"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].title").value("A"))
        .andExpect(jsonPath("$.hasNext").value(false))
        .andExpect(jsonPath("$.nextCursor").doesNotExist())
        .andExpect(jsonPath("$.nextAfter").doesNotExist());
  }

  // (3-3)
  @Test
  @DisplayName("keyword·orderBy·direction을 넘기면 조건에 맞게 돌려준다")
  void searchWithKeywordAndSort() throws Exception {
    savedBook("자바 기초");
    savedBook("파이썬 기초");
    savedBook("자바 심화");

    mockMvc
        .perform(
            get("/api/books")
                .param("keyword", "자바")
                .param("orderBy", "title")
                .param("direction", "ASC"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(2))
        .andExpect(jsonPath("$.content[0].title").value("자바 기초"))
        .andExpect(jsonPath("$.content[1].title").value("자바 심화"))
        .andExpect(jsonPath("$.totalElements").value(2));
  }

  // (3-4)
  @Test
  @DisplayName("숫자 정렬이어도 nextCursor는 문자열이다")
  void searchRatingCursorIsString() throws Exception {
    savedBook("4점").addReview(4);
    savedBook("2점").addReview(2);

    mockMvc
        .perform(get("/api/books").param("orderBy", "rating").param("limit", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].title").value("4점"))
        .andExpect(jsonPath("$.nextCursor").value("4.0"));
  }

  // (3-5)
  @Test
  @DisplayName("명세에 없는 orderBy면 400을 돌려준다")
  void searchWithUnknownOrderBy() throws Exception {
    mockMvc
        .perform(get("/api/books").param("orderBy", "author"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("COMMON_INVALID_INPUT"));
  }

  // (3-6)
  @Test
  @DisplayName("ASC·DESC가 아닌 direction이면 400을 돌려준다")
  void searchWithUnknownDirection() throws Exception {
    mockMvc
        .perform(get("/api/books").param("direction", "UP"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("COMMON_INVALID_INPUT"));
  }

  // (3-7)
  @Test
  @DisplayName("limit이 1~100을 벗어나면 400을 돌려준다")
  void searchWithInvalidLimit() throws Exception {
    mockMvc
        .perform(get("/api/books").param("limit", "0"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("COMMON_INVALID_INPUT"));
    mockMvc
        .perform(get("/api/books").param("limit", "101"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("COMMON_INVALID_INPUT"));
  }

  // (3-8)
  @Test
  @DisplayName("cursor만 있고 after가 없으면 400을 돌려준다")
  void searchWithCursorWithoutAfter() throws Exception {
    mockMvc
        .perform(get("/api/books").param("cursor", "B"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("COMMON_INVALID_INPUT"));
  }

  // (3-9)
  @Test
  @DisplayName("정렬 기준 타입에 맞지 않는 cursor면 400을 돌려준다")
  void searchWithMalformedCursor() throws Exception {
    mockMvc
        .perform(
            get("/api/books")
                .param("orderBy", "rating")
                .param("cursor", "높음")
                .param("after", "2026-01-01T00:00:01Z"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value("COMMON_INVALID_INPUT"));
  }
}
