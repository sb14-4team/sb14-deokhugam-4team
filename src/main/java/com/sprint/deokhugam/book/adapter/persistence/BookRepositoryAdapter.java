package com.sprint.deokhugam.book.adapter.persistence;

import com.sprint.deokhugam.book.application.dto.BookSearchCondition;
import com.sprint.deokhugam.book.application.required.BookPersistencePort;
import com.sprint.deokhugam.book.domain.Book;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookRepositoryAdapter implements BookPersistencePort {

  private final BookJpaRepository bookJpaRepository;
  private final BookSearchRepository bookSearchRepository;

  @Override
  public Book save(Book book) {
    return bookJpaRepository.save(book);
  }

  @Override
  public Optional<Book> findActiveById(UUID bookId) {
    return bookJpaRepository.findByIdAndDeletedAtIsNull(bookId);
  }

  @Override
  public boolean existsByIsbn(String isbn) {
    return bookJpaRepository.existsByIsbn(isbn);
  }

  @Override
  public List<Book> search(BookSearchCondition condition, int size) {
    return bookSearchRepository.search(condition, size);
  }

  @Override
  public long countByKeyword(String keyword) {
    return bookSearchRepository.countByKeyword(keyword);
  }
}
