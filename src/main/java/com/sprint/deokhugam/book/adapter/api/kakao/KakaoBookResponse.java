package com.sprint.deokhugam.book.adapter.api.kakao;

import java.util.List;

/** 카카오 책 검색 응답. 쓰는 필드만 담고 나머지는 Jackson이 무시한다. */
public record KakaoBookResponse(List<Document> documents) {

  public record Document(
      String title,
      String contents,
      String publisher,
      List<String> authors,
      String datetime, // 예: 2016-01-27T00:00:00.000+09:00
      String isbn, // 예: "8994492038 9788994492032"
      String thumbnail) {} // 이미지 URL
}
