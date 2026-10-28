package com.sprint.deokhugam.comment;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sprint.deokhugam.book.adapter.persistence.BookJpaRepository;
import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.comment.adapter.persistence.CommentJpaRepository;
import com.sprint.deokhugam.comment.application.dto.CommentCreateRequest;
import com.sprint.deokhugam.comment.application.dto.CommentUpdateRequest;
import com.sprint.deokhugam.comment.domain.Comment;
import com.sprint.deokhugam.review.adapter.persistence.ReviewJpaRepository;
import com.sprint.deokhugam.review.domain.Review;
import com.sprint.deokhugam.user.adapter.persistence.UserJpaRepository;
import com.sprint.deokhugam.user.domain.Email;
import com.sprint.deokhugam.user.domain.Password;
import com.sprint.deokhugam.user.domain.User;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class CommentIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private JsonMapper jsonMapper;
  @Autowired private BookJpaRepository bookJpaRepository;
  @Autowired private ReviewJpaRepository reviewJpaRepository;
  @Autowired private UserJpaRepository userJpaRepository;
  @Autowired private CommentJpaRepository commentJpaRepository;

  private Review savedReview;
  private Book savedBook;
  private User savedUser;
  private Comment savedComment;

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    // repository test와 달리 특별히 지시하지 않는 이상 메인 DB 설정을 임의로 바꾸지 않기 때문에 URL을 H2로 강제 덮어쓰기
    registry.add("spring.datasource.url", () -> "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
    registry.add("spring.jpa.properties.hibernate.globally_quoted_identifiers", () -> "false");
  }

  @BeforeEach
  void setUp() {
    User user = User.create("testNickname", Email.to("test@example.com"), Password.to("abcd1234!"));
    savedUser = userJpaRepository.save(user);

    Book book =
        Book.create(
            "testTitle",
            "testAuthor",
            "testDescription",
            "testPublisher",
            LocalDate.of(2011, 1, 1),
            null,
            "https://example.com/thumbnails/testTitle.jpg");
    savedBook = bookJpaRepository.save(book);

    Review review = Review.create(1, "testReview", savedUser, savedBook);
    savedReview = reviewJpaRepository.save(review);

    Comment comment = Comment.create(savedReview, savedUser, "testCreateContent");
    savedComment = commentJpaRepository.save(comment);
  }

  @Test
  @DisplayName("통합 테스트: 댓글 생성 요청 시 DB에 저장")
  void createCommentIntegration() throws Exception {
    // Given
    CommentCreateRequest request =
        new CommentCreateRequest(savedReview.getId(), savedUser.getId(), "testCreateContent");

    // When & Then
    mockMvc
        .perform(
            post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.content").value("testCreateContent"));
  }

  @Test
  @DisplayName("통합 테스트: 필수 값이 누락된 댓글 생성 요청 시 예외 처리")
  void createCommentValidationFailIntegration() throws Exception {
    CommentCreateRequest request = new CommentCreateRequest(null, null, "");

    mockMvc
        .perform(
            post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @DisplayName("통합 테스트: 댓글 수정 요청 시 변경된 내용 DB에 저장")
  void updateCommentIntegration() throws Exception {
    // Given
    CommentUpdateRequest updateRequest = new CommentUpdateRequest("testUpdatedContent");

    // When & Then
    mockMvc
        .perform(
            patch("/api/comments/{commentId}", savedComment.getId())
                .header("Deokhugam-Request-User-ID", savedUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content").value("testUpdatedContent"));
  }
}
