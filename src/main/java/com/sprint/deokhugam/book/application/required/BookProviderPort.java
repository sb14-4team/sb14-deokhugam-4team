package com.sprint.deokhugam.book.application.required;

import com.sprint.deokhugam.book.application.dto.BookInfoDto;
import java.util.Optional;

// 바깥(카카오 등)에서 ISBN으로 도서 정보를 가져온다. 없으면 빈 Optional
public interface BookProviderPort {

  Optional<BookInfoDto> findByIsbn(String isbn);
}
