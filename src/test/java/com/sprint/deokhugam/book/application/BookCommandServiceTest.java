package com.sprint.deokhugam.book.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sprint.deokhugam.book.FakeBookRepositoryPort;
import com.sprint.deokhugam.book.application.dto.BookCreateRequest;
import com.sprint.deokhugam.book.application.dto.BookDto;
import com.sprint.deokhugam.book.application.dto.BookUpdateRequest;
import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.domain.book.exception.BookErrorCode;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BookCommandServiceTest {

  private final FakeBookRepositoryPort bookRepositoryPort = new FakeBookRepositoryPort();
  private final BookCommandService bookCommandService = new BookCommandService(bookRepositoryPort);

  private BookCreateRequest request(String isbn) {
    return new BookCreateRequest(
        "클린 코드", "로버트 C. 마틴", "소개", "인사이트", LocalDate.of(2013, 12, 24), isbn);
  }

  private BookUpdateRequest updateRequest() {
    return new BookUpdateRequest("새 제목", "새 저자", "새 소개", "새 출판사", LocalDate.of(2021, 2, 2));
  }

  private Book savedBook() {
    return bookRepositoryPort.save(
        Book.create(
            "클린 코드", "로버트 C. 마틴", "소개", "인사이트", LocalDate.of(2013, 12, 24), "9788966260959", null));
  }

  // ===== (1) create() =====
  // (1-1)
  @Test
  @DisplayName("도서를 등록하면 저장되고 리뷰 수와 평점이 0인 응답을 돌려준다")
  void create() {
    BookDto bookDto = bookCommandService.create(request("9788966260959"));

    assertThat(bookRepositoryPort.count()).isEqualTo(1);
    assertThat(bookDto.title()).isEqualTo("클린 코드");
    assertThat(bookDto.isbn()).isEqualTo("9788966260959");
    assertThat(bookDto.thumbnailUrl()).isNull();
    assertThat(bookDto.reviewCount()).isZero();
    assertThat(bookDto.rating()).isZero();
  }

  // (1-2)
  @Test
  @DisplayName("이미 등록된 ISBN이면 DUPLICATE_ISBN 예외가 발생하고 저장하지 않는다")
  void createWithDuplicateIsbn() {
    bookCommandService.create(request("9788966260959"));

    assertThatThrownBy(() -> bookCommandService.create(request("9788966260959")))
        .isInstanceOf(BusinessException.class)
        .extracting("errorCode")
        .isEqualTo(BookErrorCode.DUPLICATE_ISBN);
    assertThat(bookRepositoryPort.count()).isEqualTo(1);
  }

  // (1-3)
  @Test
  @DisplayName("ISBN이 없는 도서는 여러 권 등록할 수 있다")
  void createWithoutIsbn() {
    bookCommandService.create(request(null));
    bookCommandService.create(request(null));

    assertThat(bookRepositoryPort.count()).isEqualTo(2);
  }

  // ===== (2) update() =====
  // (2-1)
  @Test
  @DisplayName("도서를 수정하면 기본 정보만 바뀌고 ISBN은 그대로다")
  void update() {
    Book saved = savedBook();

    BookDto bookDto = bookCommandService.update(saved.getId(), updateRequest());

    assertThat(bookDto.id()).isEqualTo(saved.getId());
    assertThat(bookDto.title()).isEqualTo("새 제목");
    assertThat(bookDto.author()).isEqualTo("새 저자");
    assertThat(bookDto.description()).isEqualTo("새 소개");
    assertThat(bookDto.publisher()).isEqualTo("새 출판사");
    assertThat(bookDto.publishedDate()).isEqualTo(LocalDate.of(2021, 2, 2));
    assertThat(bookDto.isbn()).isEqualTo("9788966260959");
    assertThat(bookRepositoryPort.count()).isEqualTo(1);
  }

  // (2-2)
  @Test
  @DisplayName("없는 도서를 수정하면 NOT_FOUND 예외가 발생한다")
  void updateNotFound() {
    assertThatThrownBy(() -> bookCommandService.update(UUID.randomUUID(), updateRequest()))
        .isInstanceOf(BusinessException.class)
        .extracting("errorCode")
        .isEqualTo(BookErrorCode.NOT_FOUND);
  }

  // (2-3)
  @Test
  @DisplayName("논리 삭제된 도서를 수정하면 NOT_FOUND 예외가 발생하고 값은 그대로다")
  void updateDeleted() {
    Book saved = savedBook();
    saved.softDelete();

    assertThatThrownBy(() -> bookCommandService.update(saved.getId(), updateRequest()))
        .isInstanceOf(BusinessException.class)
        .extracting("errorCode")
        .isEqualTo(BookErrorCode.NOT_FOUND);
    assertThat(saved.getTitle()).isEqualTo("클린 코드");
  }
}
