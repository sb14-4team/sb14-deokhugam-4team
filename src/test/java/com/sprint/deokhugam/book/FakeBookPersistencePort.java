package com.sprint.deokhugam.book;

import com.sprint.deokhugam.book.application.required.BookPersistencePort;
import com.sprint.deokhugam.book.domain.Book;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class FakeBookPersistencePort implements BookPersistencePort {

  private final List<Book> books = new ArrayList<>();

  @Override
  public Book save(Book book) {
    books.add(book);
    return book;
  }

  @Override
  public Optional<Book> findById(UUID bookId) {
    return books.stream().filter(book -> Objects.equals(book.getId(), bookId)).findFirst();
  }

  @Override
  public boolean existsByIsbn(String isbn) {
    return books.stream().anyMatch(book -> Objects.equals(book.getIsbn(), isbn));
  }

  public int count() {
    return books.size();
  }
}
