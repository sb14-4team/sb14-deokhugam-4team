package com.sprint.deokhugam.book;

import com.sprint.deokhugam.book.application.dto.BookInfoDto;
import com.sprint.deokhugam.book.application.required.BookProviderPort;
import com.sprint.deokhugam.book.domain.Isbn;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// 카카오 대신 메모리에 넣어 둔 도서 정보를 ISBN으로 돌려주는 가짜 포트
public class FakeBookProviderPort implements BookProviderPort {

  private final Map<String, BookInfoDto> infos = new HashMap<>();

  public void put(BookInfoDto info) {
    put(info.isbn(), info);
  }

  // 찾는 키와 응답의 isbn을 다르게 둘 때 (카카오가 10자리만 줄 때 흉내)
  public void put(String isbn, BookInfoDto info) {
    infos.put(isbn, info);
  }

  @Override
  public Optional<BookInfoDto> findByIsbn(Isbn isbn) {
    return Optional.ofNullable(infos.get(isbn.value()));
  }
}
