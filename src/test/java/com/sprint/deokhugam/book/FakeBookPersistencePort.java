package com.sprint.deokhugam.book;

import com.sprint.deokhugam.book.application.required.BookPersistencePort;
import com.sprint.deokhugam.book.domain.Book;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.springframework.test.util.ReflectionTestUtils;

public class FakeBookPersistencePort implements BookPersistencePort {

  private final List<Book> books = new ArrayList<>();

  @Override
  public Book save(Book book) {
    if (book.getId() == null) {
      // 실제 DB가 없으니 JPA 대신 여기서 id를 채운다
      ReflectionTestUtils.setField(book, "id", UUID.randomUUID());
    }
    books.add(book);
    return book;
  }

  @Override
  public Optional<Book> findActiveById(UUID bookId) {
    return books.stream()
        .filter(book -> Objects.equals(book.getId(), bookId))
        .filter(book -> !book.isDeleted())
        .findFirst();
  }

  @Override
  public boolean existsByIsbn(String isbn) {
    return books.stream().anyMatch(book -> Objects.equals(book.getIsbn(), isbn));
  }

  public int count() {
    return books.size();
  }
}
