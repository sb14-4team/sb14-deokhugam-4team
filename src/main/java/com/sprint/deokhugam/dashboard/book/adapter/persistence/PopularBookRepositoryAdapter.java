package com.sprint.deokhugam.dashboard.book.adapter.persistence;

import com.sprint.deokhugam.dashboard.book.application.required.PopularBookRepositoryPort;
import com.sprint.deokhugam.dashboard.book.domain.PopularBook;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class PopularBookRepositoryAdapter implements PopularBookRepositoryPort {
  private final PopularBookJpaRepository jpaRepository;

  @Override
  public void saveAll(List<PopularBook> rankings) {
    jpaRepository.saveAll(rankings);
  }
}
