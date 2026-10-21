package com.sprint.deokhugam.book.application.provided.query;

import com.sprint.deokhugam.book.domain.Book;
import java.util.Optional;
import java.util.UUID;

public interface BookFinder {

  Optional<Book> findById(UUID bookId);
}
