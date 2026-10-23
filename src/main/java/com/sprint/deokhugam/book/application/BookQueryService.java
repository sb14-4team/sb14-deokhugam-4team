package com.sprint.deokhugam.book.application;

import com.sprint.deokhugam.book.application.dto.BookDto;
import com.sprint.deokhugam.book.application.provided.query.BookFinder;
import com.sprint.deokhugam.book.application.required.BookPersistencePort;
import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.domain.book.exception.BookErrorCode;
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
  public Book findById(UUID bookId) {

    return bookPersistencePort
        .findActiveById(bookId)
        .orElseThrow(() -> new BusinessException(BookErrorCode.NOT_FOUND, "bookId=" + bookId));
  }

  @Override
  public BookDto getById(UUID bookId) {
    return BookDto.from(findById(bookId));
  }
}
