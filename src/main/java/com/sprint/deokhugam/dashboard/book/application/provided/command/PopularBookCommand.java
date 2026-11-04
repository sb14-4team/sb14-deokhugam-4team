package com.sprint.deokhugam.dashboard.book.application.provided.command;

import java.util.UUID;

public interface PopularBookCommand {
  void deletePopularBook(UUID bookId);
}
