package com.sprint.deokhugam.book.adapter.persistence;

import com.sprint.deokhugam.book.application.required.BookPersistencePort;
import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.domain.book.exception.BookErrorCode;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookRepositoryAdapter implements BookPersistencePort {

  private final BookJpaRepository bookJpaRepository;

  // 동시에 같은 ISBN이 들어오면 DB unique 제약이 막는다. 바로 INSERT해야 여기서 잡아 409로 바꿀 수 있다
  @Override
  public Book save(Book book) {
    try {
      return bookJpaRepository.saveAndFlush(book);
    } catch (DataIntegrityViolationException e) {
      throw new BusinessException(BookErrorCode.DUPLICATE_ISBN, "isbn=" + book.getIsbn(), e);
    }
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
