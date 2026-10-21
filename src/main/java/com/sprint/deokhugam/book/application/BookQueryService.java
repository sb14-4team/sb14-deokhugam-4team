package com.sprint.deokhugam.book.application;

import com.sprint.deokhugam.book.application.provided.query.BookFinder;
import com.sprint.deokhugam.book.domain.Book;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class BookQueryService implements BookFinder {

  @Override
  public Book findById(UUID userId) {
    return null;
  }
}
