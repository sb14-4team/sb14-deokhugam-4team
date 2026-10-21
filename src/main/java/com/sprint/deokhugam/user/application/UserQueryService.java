package com.sprint.deokhugam.user.application;

import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.domain.user.exception.UserErrorCode;
import com.sprint.deokhugam.user.application.provided.query.UserFinder;
import com.sprint.deokhugam.user.application.required.UserRepository;
import com.sprint.deokhugam.user.domain.Email;
import com.sprint.deokhugam.user.domain.User;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserQueryService implements UserFinder {

  private final UserRepository userRepository;

  @Override
  public User findById(UUID userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
  }

  @Override
  public User findByEmail(Email email) {
    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
  }
}
