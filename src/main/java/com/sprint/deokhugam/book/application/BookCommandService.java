package com.sprint.deokhugam.book.application;

import com.sprint.deokhugam.book.application.dto.BookCreateRequest;
import com.sprint.deokhugam.book.application.dto.BookDto;
import com.sprint.deokhugam.book.application.provided.command.BookCommand;
import com.sprint.deokhugam.book.application.required.BookPersistencePort;
import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.domain.book.exception.BookErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BookCommandService implements BookCommand {

  private final BookPersistencePort bookPersistencePort;

  @Override
  public BookDto create(BookCreateRequest request) {
    String isbn = request.isbn();
    if (isbn != null && bookPersistencePort.existsByIsbn(isbn)) {
      throw new BusinessException(BookErrorCode.DUPLICATE_ISBN, "isbn=" + isbn);
    }

    Book book =
        Book.create(
            request.title(),
            request.author(),
            request.description(),
            request.publisher(),
            request.publishedDate(),
            isbn,
            null);
    return BookDto.from(bookPersistencePort.save(book));
  }
}
