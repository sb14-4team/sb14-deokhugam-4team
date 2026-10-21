package com.sprint.deokhugam.book.application.required;

import com.sprint.deokhugam.book.domain.Book;
import java.util.Optional;
import java.util.UUID;

public interface BookPersistencePort {

  Book save(Book book);

  Optional<Book> findById(UUID bookId);

  boolean existsByIsbn(String isbn);
}
