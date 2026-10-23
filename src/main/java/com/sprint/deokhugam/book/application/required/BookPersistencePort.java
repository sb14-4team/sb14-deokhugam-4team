package com.sprint.deokhugam.book.application.required;

import com.sprint.deokhugam.book.application.dto.BookSearchCondition;
import com.sprint.deokhugam.book.domain.Book;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookPersistencePort {

  Book save(Book book);

  // 논리 삭제된 도서는 빼고 찾는다
  Optional<Book> findActiveById(UUID bookId);

  boolean existsByIsbn(String isbn);

  // 검색 조건에 맞는 활성 도서를 정렬 순서대로, 커서 다음부터 size개까지
  List<Book> search(BookSearchCondition condition, int size);

  // 키워드에 맞는 활성 도서 전체 수 (응답의 totalElements). 커서는 상관없다
  long countByKeyword(String keyword);
}
