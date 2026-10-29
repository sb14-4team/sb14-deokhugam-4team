package com.sprint.deokhugam.book.application.dto;

import java.time.LocalDate;

/** ISBN으로 외부에서 조회한 도서 정보. 등록 폼을 채우는 용도. */
public record BookInfoDto(
    String title,
    String author,
    String description,
    String publisher,
    LocalDate publishedDate,
    String isbn,
    byte[] thumbnailImage) { // Jackson이 Base64 문자열로 내보낸다

  public BookInfoDto withIsbn(String isbn) {
    return new BookInfoDto(
        title, author, description, publisher, publishedDate, isbn, thumbnailImage);
  }
}
