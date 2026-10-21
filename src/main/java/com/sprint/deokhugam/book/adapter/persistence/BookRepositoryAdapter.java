package com.sprint.deokhugam.book.adapter.persistence;

import com.sprint.deokhugam.book.application.required.BookPersistencePort;
import com.sprint.deokhugam.book.domain.Book;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookRepositoryAdapter implements BookPersistencePort {

  private final BookJpaRepository bookJpaRepository;

  @Override
  public Book save(Book book) {
    return bookJpaRepository.save(book);
  }

  @Override
  public Optional<Book> findById(UUID bookId) {
    return bookJpaRepository.findById(bookId);
  }

  @Override
  public boolean existsByIsbn(String isbn) {
    return bookJpaRepository.existsByIsbn(isbn);
  }
}
