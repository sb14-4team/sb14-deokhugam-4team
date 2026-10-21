package com.sprint.deokhugam.book.application;

import com.sprint.deokhugam.book.application.provided.query.BookFinder;
import com.sprint.deokhugam.book.application.required.BookPersistencePort;
import com.sprint.deokhugam.book.domain.Book;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookQueryService implements BookFinder {

  private final BookPersistencePort bookPersistencePort;

  @Override
  public Optional<Book> findById(UUID bookId) {
    return bookPersistencePort.findById(bookId);
  }
}
