package com.sprint.deokhugam.book.application.provided.command;

import com.sprint.deokhugam.book.application.dto.BookCreateRequest;
import com.sprint.deokhugam.book.application.dto.BookDto;
import com.sprint.deokhugam.book.application.dto.BookUpdateRequest;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

public interface BookCommand {

  BookDto create(BookCreateRequest request, MultipartFile thumbnailImage);

  // 없는 도서, 논리 삭제된 도서는 BOOK_NOT_FOUND
  BookDto update(UUID bookId, BookUpdateRequest request, MultipartFile thumbnailImage);

  // 논리 삭제. 리뷰, 댓글, 썸네일 파일은 그대로 둔다
  void delete(UUID bookId);

  // 물리 삭제. 리뷰와 그 아래 데이터, 썸네일 파일까지 지운다. UI에는 없고 테스트로만 부른다
  void hardDelete(UUID bookId);
}
