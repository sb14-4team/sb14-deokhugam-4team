package com.sprint.deokhugam.user.application.required;

import com.sprint.deokhugam.user.domain.Email;
import com.sprint.deokhugam.user.domain.User;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

  User save(User user);

  boolean existsByEmail(Email email);

  Optional<User> findByEmail(Email email);

  Optional<User> findById(UUID userId);

  List<UUID> findCleanupIds(Instant cutoff, int limit);

  void remove(UUID userId);
}
