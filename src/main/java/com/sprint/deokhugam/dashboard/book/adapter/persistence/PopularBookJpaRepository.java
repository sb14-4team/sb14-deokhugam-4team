package com.sprint.deokhugam.dashboard.book.adapter.persistence;

import com.sprint.deokhugam.dashboard.book.domain.PopularBook;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PopularBookJpaRepository extends JpaRepository<PopularBook, UUID> {}
