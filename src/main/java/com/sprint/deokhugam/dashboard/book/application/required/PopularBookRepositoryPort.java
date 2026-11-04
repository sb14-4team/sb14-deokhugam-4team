package com.sprint.deokhugam.dashboard.book.application.required;

import com.sprint.deokhugam.dashboard.book.domain.PopularBook;
import java.util.List;
import java.util.UUID;

public interface PopularBookRepositoryPort {
  void saveAll(List<PopularBook> rankings);

  void deleteByBookId(UUID bookId);
}
