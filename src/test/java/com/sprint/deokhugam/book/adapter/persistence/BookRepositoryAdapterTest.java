package com.sprint.deokhugam.book.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.common.config.JpaConfig;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

// 논리 삭제 여부에 따라 두 조회가 다르게 동작하는지만 본다
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import({JpaConfig.class, BookRepositoryAdapter.class})
class BookRepositoryAdapterTest {

  @Autowired private BookRepositoryAdapter bookRepositoryAdapter;

  private Book saved() {
    Book book =
        Book.create("클린 코드", "로버트 C. 마틴", "소개", "인사이트", LocalDate.of(2013, 12, 24), null, null);
    return bookRepositoryAdapter.save(book);
  }

  // (1-1)
  @Test
  @DisplayName("논리 삭제된 도서는 findActiveById로는 안 보이고 findById로는 보인다")
  void deletedBook() {
    Book book = saved();
    book.softDelete();

    assertThat(bookRepositoryAdapter.findActiveById(book.getId())).isEmpty();
    assertThat(bookRepositoryAdapter.findById(book.getId())).contains(book);
  }

  // (1-2)
  @Test
  @DisplayName("활성 도서는 두 조회 모두에서 보인다")
  void activeBook() {
    Book book = saved();

    assertThat(bookRepositoryAdapter.findActiveById(book.getId())).contains(book);
    assertThat(bookRepositoryAdapter.findById(book.getId())).contains(book);
  }
}
