package com.sprint.deokhugam.book.application;

import com.sprint.deokhugam.book.application.dto.BookCreateRequest;
import com.sprint.deokhugam.book.application.dto.BookDto;
import com.sprint.deokhugam.book.application.dto.BookUpdateRequest;
import com.sprint.deokhugam.book.application.provided.command.BookCommand;
import com.sprint.deokhugam.book.application.required.BookPersistencePort;
import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.domain.book.exception.BookErrorCode;
import java.util.UUID;
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

  @Override
  public BookDto update(UUID bookId, BookUpdateRequest request) {
    Book book =
        bookPersistencePort
            .findActiveById(bookId)
            .orElseThrow(() -> new BusinessException(BookErrorCode.NOT_FOUND, "bookId=" + bookId));

    // 트랜잭션이 끝날 때 JPA가 바뀐 값을 알아서 UPDATE 한다 (변경 감지). save()를 따로 부르지 않는다
    book.update(
        request.title(),
        request.author(),
        request.description(),
        request.publisher(),
        request.publishedDate());
    return BookDto.from(book);
  }
}
