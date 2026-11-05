package com.sprint.deokhugam.user.adapter.persistence;

import com.sprint.deokhugam.user.domain.Email;
import com.sprint.deokhugam.user.domain.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserJpaRepository extends JpaRepository<User, UUID> {

  @Query(
      """
      select u.id
      from User u
      where u.rankingStats is not null
      order by u.id
      """)
  List<UUID> findRankingTargetIds();

  boolean existsByEmail(Email email);

  Optional<User> findByEmail(Email email);
}
