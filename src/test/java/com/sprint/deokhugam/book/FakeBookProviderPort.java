package com.sprint.deokhugam.book;

import com.sprint.deokhugam.book.application.dto.BookInfoDto;
import com.sprint.deokhugam.book.application.required.BookProviderPort;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// 카카오 대신 메모리에 넣어 둔 도서 정보를 ISBN으로 돌려주는 가짜 포트
public class FakeBookProviderPort implements BookProviderPort {

  private final Map<String, BookInfoDto> infos = new HashMap<>();

  public void put(BookInfoDto info) {
    infos.put(info.isbn(), info);
  }

  @Override
  public Optional<BookInfoDto> findByIsbn(String isbn) {
    return Optional.ofNullable(infos.get(isbn));
  }
}
