package com.sprint.deokhugam.book.adapter.persistence;

import com.sprint.deokhugam.book.domain.Book;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookJpaRepository extends JpaRepository<Book, UUID> {

  boolean existsByIsbn(String isbn);
}
