package com.sprint.deokhugam.book.application.required;

import com.sprint.deokhugam.book.application.dto.BookSearchCondition;
import com.sprint.deokhugam.book.domain.Book;
import java.util.List;

// 도서 목록 검색 (키워드·정렬·커서처럼 조건이 요청마다 달라지는 쿼리). 기본 CRUD는 BookRepositoryPort
public interface BookSearchPort {

  // 검색 조건에 맞는 활성 도서를 정렬 순서대로, 커서 다음부터 size개까지
  List<Book> search(BookSearchCondition condition, int size);

  // 키워드에 맞는 활성 도서 전체 수 (응답의 totalElements). 커서는 상관없다
  long countByKeyword(String keyword);
}
