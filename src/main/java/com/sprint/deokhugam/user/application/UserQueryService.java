package com.sprint.deokhugam.user.application;

import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.domain.user.exception.UserErrorCode;
import com.sprint.deokhugam.user.application.dto.UserDto;
import com.sprint.deokhugam.user.application.dto.UserLoginRequest;
import com.sprint.deokhugam.user.application.provided.query.UserFinder;
import com.sprint.deokhugam.user.application.provided.query.UserLogin;
import com.sprint.deokhugam.user.application.required.PasswordHasher;
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
public class UserQueryService implements UserFinder, UserLogin {

  private final UserRepository userRepository;
  private final PasswordHasher passwordHasher;

  @Override
  public User findById(UUID userId) {
    return userRepository
        .findById(userId)
        .filter(user -> !user.isDeleted())
        .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
  }

  @Override
  public UserDto getById(UUID userId) {
    User user = findById(userId);
    return UserDto.to(
        user.getId(), user.getEmail().value(), user.getNickname(), user.getCreatedAt());
  }

  @Override
  public UserDto login(UserLoginRequest request) {
    Email email = Email.to(request.email());
    User user = findByEmail(email);
    boolean matches = passwordHasher.matches(request.password(), user.getPassword().password());
    if (!matches) {
      throw new BusinessException(UserErrorCode.LOGIN_FAILED);
    }
    return UserDto.to(
        user.getId(), user.getEmail().value(), user.getNickname(), user.getCreatedAt());
  }

  private User findByEmail(Email email) {
    return userRepository
        .findByEmail(email)
        .filter(user -> !user.isDeleted())
        .orElseThrow(() -> new BusinessException(UserErrorCode.LOGIN_FAILED));
  }
}
