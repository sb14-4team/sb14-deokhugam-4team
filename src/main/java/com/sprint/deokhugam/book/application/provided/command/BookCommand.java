package com.sprint.deokhugam.book.application.provided.command;

import com.sprint.deokhugam.book.application.dto.BookCreateRequest;
import com.sprint.deokhugam.book.application.dto.BookDto;

public interface BookCommand {

  BookDto create(BookCreateRequest request);
}
