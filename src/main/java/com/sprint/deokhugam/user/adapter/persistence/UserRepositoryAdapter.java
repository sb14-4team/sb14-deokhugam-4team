package com.sprint.deokhugam.user.adapter.persistence;

import com.sprint.deokhugam.user.application.required.UserRepository;
import com.sprint.deokhugam.user.domain.User;
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
}
