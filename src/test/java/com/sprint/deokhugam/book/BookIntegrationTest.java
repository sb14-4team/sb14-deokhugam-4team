package com.sprint.deokhugam.book;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.deokhugam.book.adapter.persistence.BookJpaRepository;
import com.sprint.deokhugam.book.application.provided.query.BookFinder;
import com.sprint.deokhugam.book.application.required.BookPersistencePort;
import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.common.exception.domain.BookErrorCode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class BookIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private BookJpaRepository bookJpaRepository;
  @Autowired private BookPersistencePort bookPersistencePort;
  @Autowired private BookFinder bookFinder;

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

  private MockMultipartFile bookData(String json) {
    return new MockMultipartFile(
        "bookData", "", MediaType.APPLICATION_JSON_VALUE, json.getBytes(StandardCharsets.UTF_8));
  }

  private Book book(String isbn) {
    return Book.create("제목", "저자", "소개", "출판사", LocalDate.of(2020, 1, 1), isbn, null);
  }

  @Test
  @DisplayName("도서를 등록하면 DB에 저장되고 id와 생성 시각이 채워진 응답을 돌려준다")
  void create() throws Exception {
    mockMvc
        .perform(multipart("/api/books").file(bookData(VALID_JSON)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.createdAt").isNotEmpty())
        .andExpect(jsonPath("$.title").value("클린 코드"))
        .andExpect(jsonPath("$.thumbnailUrl").doesNotExist());

    assertThat(bookJpaRepository.count()).isEqualTo(1);
    assertThat(bookJpaRepository.existsByIsbn("9788966260959")).isTrue();
  }

  @Test
  @DisplayName("이미 등록된 ISBN으로 등록하면 409를 돌려주고 저장하지 않는다")
  void createWithDuplicateIsbn() throws Exception {
    mockMvc.perform(multipart("/api/books").file(bookData(VALID_JSON)));

    mockMvc
        .perform(multipart("/api/books").file(bookData(VALID_JSON)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value("BOOK_DUPLICATE_ISBN"));

    assertThat(bookJpaRepository.count()).isEqualTo(1);
  }

  @Test
  @DisplayName("필수 값이 없으면 400을 돌려주고 저장하지 않는다")
  void createWithoutRequiredField() throws Exception {
    String json = VALID_JSON.replace("\"publishedDate\": \"2013-12-24\",", "");

    mockMvc
        .perform(multipart("/api/books").file(bookData(json)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.details.publishedDate").value("출간일은 필수입니다."));

    assertThat(bookJpaRepository.count()).isZero();
  }

  @Test
  @DisplayName("ISBN 없이 여러 권 등록할 수 있다")
  void createWithoutIsbn() throws Exception {
    String json = VALID_JSON.replace(",\n  \"isbn\": \"9788966260959\"", "");

    mockMvc.perform(multipart("/api/books").file(bookData(json))).andExpect(status().isCreated());
    mockMvc.perform(multipart("/api/books").file(bookData(json))).andExpect(status().isCreated());

    assertThat(bookJpaRepository.count()).isEqualTo(2);
  }

  @Test
  @DisplayName("중복 확인을 거치지 않아도 DB unique 제약이 막고 DUPLICATE_ISBN으로 바꾼다")
  void uniqueConstraint() {
    bookPersistencePort.save(book("9788966260959"));

    assertThatThrownBy(() -> bookPersistencePort.save(book("9788966260959")))
        .isInstanceOf(BusinessException.class)
        .extracting("errorCode")
        .isEqualTo(BookErrorCode.DUPLICATE_ISBN);
  }

  @Test
  @DisplayName("BookFinder로 저장한 도서를 찾고, 없는 id면 빈 값을 돌려준다")
  void findById() {
    Book saved = bookPersistencePort.save(book(null));

    assertThat(bookFinder.findById(saved.getId())).get().isEqualTo(saved);
    assertThat(bookFinder.findById(UUID.randomUUID())).isEmpty();
  }
}
