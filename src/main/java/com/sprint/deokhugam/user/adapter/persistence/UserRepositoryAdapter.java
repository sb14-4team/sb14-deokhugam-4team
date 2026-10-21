package com.sprint.deokhugam.user.adapter.persistence;

import com.sprint.deokhugam.user.application.required.UserRepository;
import com.sprint.deokhugam.user.domain.Email;
import com.sprint.deokhugam.user.domain.User;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

  private final UserJpaRepository userJpaRepository;

  @Override
  public User save(User user) {
    return userJpaRepository.save(user);
  }

  @Override
  public boolean existsByEmail(Email email) {
    return userJpaRepository.existsByEmail(email);
  }

  @Override
  public Optional<User> findByEmail(Email email) {
    return userJpaRepository.findByEmail(email);
  }

  @Override
  public Optional<User> findById(UUID userId) {
    return userJpaRepository.findById(userId);
  }
}
