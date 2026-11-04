package com.sprint.deokhugam.dashboard.book.application.provided.command;

import com.sprint.deokhugam.dashboard.book.application.required.PopularBookRepositoryPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PopularBookCommandService implements PopularBookCommand {
  private final PopularBookRepositoryPort popularBookRepositoryPort;

  @Override
  @Transactional
  public void deletePopularBook(UUID bookId) {
    popularBookRepositoryPort.deleteByBookId(bookId);
  }
}
