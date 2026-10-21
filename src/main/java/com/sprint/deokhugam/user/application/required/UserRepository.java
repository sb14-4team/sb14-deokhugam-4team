package com.sprint.deokhugam.user.application.required;

import com.sprint.deokhugam.user.domain.Email;
import com.sprint.deokhugam.user.domain.User;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

  User save(User user);

  boolean existsByEmail(Email email);

  Optional<User> findByEmail(Email email);

  Optional<User> findById(UUID userId);
}
