package com.sprint.deokhugam.book.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.domain.book.exception.BookErrorCode;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

class BookRepositoryAdapterTest {

  private final BookJpaRepository bookJpaRepository = mock(BookJpaRepository.class);
  private final BookRepositoryAdapter bookRepositoryAdapter =
      new BookRepositoryAdapter(bookJpaRepository);

  @Test
  @DisplayName("DB unique 제약에 걸리면 DUPLICATE_ISBN 예외로 바꿔 던진다")
  void saveWithDuplicateIsbn() {
    Book book =
        Book.create(
            "클린 코드", "로버트 C. 마틴", "소개", "인사이트", LocalDate.of(2013, 12, 24), "9788966260959", null);
    given(bookJpaRepository.saveAndFlush(book))
        .willThrow(new DataIntegrityViolationException("duplicate key"));

    assertThatThrownBy(() -> bookRepositoryAdapter.save(book))
        .isInstanceOf(BusinessException.class)
        .extracting("errorCode")
        .isEqualTo(BookErrorCode.DUPLICATE_ISBN);
  }
}
