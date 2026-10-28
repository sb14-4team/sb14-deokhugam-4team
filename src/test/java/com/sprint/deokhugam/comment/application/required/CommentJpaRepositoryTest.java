package com.sprint.deokhugam.comment.application.required;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.comment.adapter.persistence.CommentJpaRepository;
import com.sprint.deokhugam.comment.domain.Comment;
import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.domain.comment.exception.CommentErrorCode;
import com.sprint.deokhugam.review.domain.Review;
import com.sprint.deokhugam.user.domain.Email;
import com.sprint.deokhugam.user.domain.Password;
import com.sprint.deokhugam.user.domain.User;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@DataJpaTest
@EnableJpaAuditing
@ActiveProfiles("test")
public class CommentJpaRepositoryTest {

  @Autowired private CommentJpaRepository commentJpaRepository;
  @Autowired private TestEntityManager entityManager;

  private Review review;
  private User user;

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    // 전역 따옴표("text")로 인한 DDL 테이블 생성 실패 해결를 위해 전역 따옴표 비활성화
    registry.add("spring.jpa.properties.hibernate.globally_quoted_identifiers", () -> "false");
  }

  @BeforeEach
  void setUp() {
    user = User.create("testNickname", Email.to("test@example.com"), Password.to("abcd1234!"));
    entityManager.persist(user);

    Book book =
        Book.create(
            "testTitle",
            "testAuthor",
            "testDescription",
            "testPublisher",
            LocalDate.of(2011, 1, 1),
            null,
            "https://example.com/thumbnails/testTitle.jpg");
    entityManager.persist(book);

    review = Review.create(1, "testReview", user, book);
    entityManager.persist(review);

    entityManager.flush();
    entityManager.clear();
  }

  @Test
  @DisplayName("댓글 저장 및 ID 조회 성공")
  void saveAndFindById() {
    // Given
    Comment comment = Comment.create(review, user, "testCreateContent");
    Comment savedComment = commentJpaRepository.save(comment);

    // When
    Comment foundComment = commentJpaRepository.getByIdOrThrow(savedComment.getId());

    // Then
    assertThat(foundComment).isNotNull();
    assertThat(foundComment.getContent()).isEqualTo("testCreateContent");
  }

  @Test
  @DisplayName("존재하지 않는 댓글 조회 시 예외 발생")
  void findByIdNotFoundThrowsException() {
    // Given
    UUID nonExistentId = UUID.randomUUID();

    // When & Then
    assertThatThrownBy(() -> commentJpaRepository.getByIdOrThrow(nonExistentId))
        .isInstanceOf(BusinessException.class)
        .satisfies(
            exception -> {
              BusinessException businessException = (BusinessException) exception;
              assertThat(businessException.getErrorCode()).isEqualTo(CommentErrorCode.NOT_FOUND);
            });
  }

  @Test
  @DisplayName("댓글 수정 성공")
  void updateComment() {
    // Given
    Comment comment = Comment.create(review, user, "testCreateContent");
    Comment savedComment = commentJpaRepository.save(comment);

    // When
    Comment foundComment = commentJpaRepository.getByIdOrThrow(savedComment.getId());
    foundComment.update("testUpdatedContent");

    // Then
    Comment updatedComment = commentJpaRepository.getByIdOrThrow(savedComment.getId());
    assertThat(updatedComment.getContent()).isEqualTo("testUpdatedContent");
  }

  @Test
  @DisplayName("유저 ID로 댓글 리스트 조회 성공")
  void findByUser_Id() {
    // Given
    Comment comment1 = Comment.create(review, user, "testComment1");
    Comment comment2 = Comment.create(review, user, "testComment2");
    commentJpaRepository.save(comment1);
    commentJpaRepository.save(comment2);

    // When
    List<Comment> comments = commentJpaRepository.findByUser_Id(user.getId());

    // Then
    assertThat(comments).hasSize(2);
    assertThat(comments)
        .extracting(Comment::getContent)
        .containsExactlyInAnyOrder("testComment1", "testComment2");
  }
}
