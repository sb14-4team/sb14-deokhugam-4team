package com.sprint.deokhugam.book.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sprint.deokhugam.book.FakeBookPersistencePort;
import com.sprint.deokhugam.book.application.dto.BookCreateRequest;
import com.sprint.deokhugam.book.application.dto.BookDto;
import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.domain.book.exception.BookErrorCode;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BookCommandServiceTest {

  private final FakeBookPersistencePort bookPersistencePort = new FakeBookPersistencePort();
  private final BookCommandService bookCommandService = new BookCommandService(bookPersistencePort);

  private BookCreateRequest request(String isbn) {
    return new BookCreateRequest(
        "클린 코드", "로버트 C. 마틴", "소개", "인사이트", LocalDate.of(2013, 12, 24), isbn);
  }

  // ===== (1) create() =====
  // (1-1)
  @Test
  @DisplayName("도서를 등록하면 저장되고 리뷰 수와 평점이 0인 응답을 돌려준다")
  void create() {
    BookDto bookDto = bookCommandService.create(request("9788966260959"));

    assertThat(bookPersistencePort.count()).isEqualTo(1);
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
    assertThat(bookPersistencePort.count()).isEqualTo(1);
  }

  // (1-3)
  @Test
  @DisplayName("ISBN이 없는 도서는 여러 권 등록할 수 있다")
  void createWithoutIsbn() {
    bookCommandService.create(request(null));
    bookCommandService.create(request(null));

    assertThat(bookPersistencePort.count()).isEqualTo(2);
  }
}
