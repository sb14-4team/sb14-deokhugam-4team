package com.sprint.deokhugam.book.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sprint.deokhugam.book.FakeBookPersistencePort;
import com.sprint.deokhugam.book.application.dto.BookDto;
import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.domain.book.exception.BookErrorCode;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BookQueryServiceTest {

  private final FakeBookPersistencePort bookPersistencePort = new FakeBookPersistencePort();
  private final BookQueryService bookQueryService = new BookQueryService(bookPersistencePort);

  private Book savedBook() {
    return bookPersistencePort.save(
        Book.create(
            "클린 코드", "로버트 C. 마틴", "소개", "인사이트", LocalDate.of(2013, 12, 24), "9788966260959", null));
  }

  // ===== (1) findById() : Book 엔티티 반환 =====
  // (1-1)
  @Test
  @DisplayName("id로 도서를 찾으면 저장된 도서를 돌려준다")
  void findById() {
    Book saved = savedBook();

    assertThat(bookQueryService.findById(saved.getId())).isEqualTo(saved);
  }

  // ===== (2) getById() : BookDto 반환 =====
  // (2-1)
  @Test
  @DisplayName("id로 도서 상세를 조회하면 도서 정보 응답을 돌려준다")
  void getById() {
    Book saved = savedBook();

    BookDto bookDto = bookQueryService.getById(saved.getId());

    assertThat(bookDto.id()).isEqualTo(saved.getId());
    assertThat(bookDto.title()).isEqualTo("클린 코드");
    assertThat(bookDto.isbn()).isEqualTo("9788966260959");
    assertThat(bookDto.reviewCount()).isZero();
    assertThat(bookDto.rating()).isZero();
  }

  // (2-2)
  @Test
  @DisplayName("없는 id로 조회하면 NOT_FOUND 예외가 발생한다")
  void getByIdNotFound() {
    assertThatThrownBy(() -> bookQueryService.getById(UUID.randomUUID()))
        .isInstanceOf(BusinessException.class)
        .extracting("errorCode")
        .isEqualTo(BookErrorCode.NOT_FOUND);
  }

  // (2-3)
  @Test
  @DisplayName("논리 삭제된 도서는 없는 것으로 보고 NOT_FOUND 예외가 발생한다")
  void getByIdDeleted() {
    Book saved = savedBook();
    saved.softDelete();

    assertThatThrownBy(() -> bookQueryService.getById(saved.getId()))
        .isInstanceOf(BusinessException.class)
        .extracting("errorCode")
        .isEqualTo(BookErrorCode.NOT_FOUND);
  }
}
