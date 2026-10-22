package com.sprint.deokhugam.book.application.provided.query;

import com.sprint.deokhugam.book.application.dto.BookDto;
import com.sprint.deokhugam.book.domain.Book;
import java.util.UUID;

// 논리 삭제된 도서는 둘 다 없는 것으로 본다 (BOOK_NOT_FOUND)
public interface BookFinder {

  // 다른 도메인(리뷰 등)이 도서 엔티티가 필요할 때
  Book findById(UUID bookId);

  // 도서 상세 조회 API 응답용
  BookDto getById(UUID bookId);
}
