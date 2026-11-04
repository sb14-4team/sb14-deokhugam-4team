package com.sprint.deokhugam.dashboard.book.domain;

import java.util.UUID;

// 흠 ...
public record BookDeletingEvent(UUID bookId) {
  public static BookDeletingEvent of(UUID bookId) {
    return new BookDeletingEvent(bookId);
  }
}
