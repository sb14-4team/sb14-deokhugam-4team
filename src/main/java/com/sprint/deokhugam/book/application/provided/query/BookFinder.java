package com.sprint.deokhugam.book.application.provided.query;

import com.sprint.deokhugam.book.domain.Book;
import java.util.UUID;

public interface BookFinder {

  Book findById(UUID bookId);
}
