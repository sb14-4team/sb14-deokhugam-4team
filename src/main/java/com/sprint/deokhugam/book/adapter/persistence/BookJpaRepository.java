package com.sprint.deokhugam.book.adapter.persistence;

import com.sprint.deokhugam.book.domain.Book;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookJpaRepository extends JpaRepository<Book, UUID> {

  boolean existsByIsbn(String isbn);

  // deletedAt이 null인 행만 → 논리 삭제 제외
  Optional<Book> findByIdAndDeletedAtIsNull(UUID bookId);
}
