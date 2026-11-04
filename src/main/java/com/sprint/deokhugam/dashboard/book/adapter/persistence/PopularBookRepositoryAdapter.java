package com.sprint.deokhugam.dashboard.book.adapter.persistence;

import com.sprint.deokhugam.dashboard.book.application.provided.query.PopularBookSearchCondition;
import com.sprint.deokhugam.dashboard.book.application.required.PopularBookQueryPort;
import com.sprint.deokhugam.dashboard.book.application.required.PopularBookRepositoryPort;
import com.sprint.deokhugam.dashboard.book.domain.PopularBook;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PopularBookRepositoryAdapter
    implements PopularBookRepositoryPort, PopularBookQueryPort {
  private final PopularBookJpaRepository popularBookJpaRepository;
  private final PopularBookQuerydslRepository popularBookQuerydslRepository;

  @Override
  public void saveAll(List<PopularBook> rankings) {
    popularBookJpaRepository.saveAll(rankings);
  }

  @Override
  public List<PopularBook> search(PopularBookSearchCondition condition) {
    return popularBookQuerydslRepository.searchByCondition(condition);
  }

  @Override
  public void deleteByBookId(UUID bookId) {
    popularBookQuerydslRepository.deleteByBookId(bookId);
  }
}
