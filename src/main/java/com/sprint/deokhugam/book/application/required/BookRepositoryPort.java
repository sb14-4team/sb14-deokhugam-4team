package com.sprint.deokhugam.book.application.required;

import com.sprint.deokhugam.book.domain.Book;
import java.util.Optional;
import java.util.UUID;

// 도서 저장·단건 조회·중복 확인 (기본 CRUD). 조건 검색은 BookSearchPort
public interface BookRepositoryPort {

  Book save(Book book);

  // 논리 삭제된 도서는 빼고 찾는다
  Optional<Book> findActiveById(UUID bookId);

  boolean existsByIsbn(String isbn);
}
