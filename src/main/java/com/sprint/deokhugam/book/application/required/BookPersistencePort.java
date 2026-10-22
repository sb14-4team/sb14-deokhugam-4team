package com.sprint.deokhugam.book.application.required;

import com.sprint.deokhugam.book.domain.Book;
import java.util.Optional;
import java.util.UUID;

public interface BookPersistencePort {

  Book save(Book book);

  // 논리 삭제된 도서는 빼고 찾는다
  Optional<Book> findActiveById(UUID bookId);

  boolean existsByIsbn(String isbn);
}
