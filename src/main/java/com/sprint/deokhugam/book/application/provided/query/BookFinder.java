package com.sprint.deokhugam.book.application.provided.query;

import com.sprint.deokhugam.book.application.dto.BookDto;
import com.sprint.deokhugam.book.application.dto.BookInfoDto;
import com.sprint.deokhugam.book.application.dto.BookSearchCondition;
import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.common.pagination.CursorPageResponse;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

// 논리 삭제된 도서는 모두 없는 것으로 본다 (단건은 BOOK_NOT_FOUND, 목록은 제외)
public interface BookFinder {

  // 다른 도메인(리뷰 등)이 도서 엔티티가 필요할 때
  Book findById(UUID bookId);

  // 도서 상세 조회 API 응답용
  BookDto getById(UUID bookId);

  // 도서 목록 조회 API 응답용. limit개씩 커서 페이지로 돌려준다
  CursorPageResponse<BookDto> search(BookSearchCondition condition, int limit);

  // ISBN으로 등록 폼에 채울 도서 정보 조회. 없으면 BOOK_INFO_NOT_FOUND
  BookInfoDto getBookInfo(String isbn);

  // 도서 이미지에서 ISBN 하나를 읽는다. 못 읽으면 BOOK_OCR_FAILED(400)
  // provided 포트는 웹 어댑터만 부르므로 MultipartFile을 받는다 (required 포트에는 들이지 않는다)
  String recognizeIsbn(MultipartFile image);
}
