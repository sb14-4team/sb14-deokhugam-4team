package com.sprint.deokhugam.dashboard.book.adapter.persistence;

import com.sprint.deokhugam.dashboard.book.application.PopularBookSearchCondition;
import com.sprint.deokhugam.dashboard.book.application.required.PopularBookQueryPort;
import com.sprint.deokhugam.dashboard.book.application.required.PopularBookRepositoryPort;
import com.sprint.deokhugam.dashboard.book.domain.PopularBook;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PopularBookRepositoryAdapter
    implements PopularBookRepositoryPort, PopularBookQueryPort {
  private final PopularBookJpaRepository jpaRepository;
  private final PopularBookQuerydslRepository popularBookQuerydslRepository;

  @Override
  public void saveAll(List<PopularBook> rankings) {
    jpaRepository.saveAll(rankings);
  }

  @Override
  public List<PopularBook> search(PopularBookSearchCondition condition) {
    return popularBookQuerydslRepository.searchByCondition(condition);
  }
}
