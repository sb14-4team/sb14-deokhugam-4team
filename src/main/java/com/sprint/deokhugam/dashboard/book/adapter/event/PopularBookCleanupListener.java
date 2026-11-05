package com.sprint.deokhugam.dashboard.book.adapter.event;

import com.sprint.deokhugam.dashboard.book.application.provided.command.PopularBookCommand;
import com.sprint.deokhugam.dashboard.book.domain.BookDeletingEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PopularBookCleanupListener {
  private final PopularBookCommand popularBookCommand;

  @EventListener
  public void handle(BookDeletingEvent event) {
    popularBookCommand.deletePopularBook(event.bookId());
  }
}
