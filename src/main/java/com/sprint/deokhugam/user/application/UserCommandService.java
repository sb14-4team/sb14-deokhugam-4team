package com.sprint.deokhugam.user.application;

import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.domain.user.exception.UserErrorCode;
import com.sprint.deokhugam.user.application.dto.UserDto;
import com.sprint.deokhugam.user.application.dto.UserRegisterRequest;
import com.sprint.deokhugam.user.application.provided.command.UserCommand;
import com.sprint.deokhugam.user.application.required.PasswordHasher;
import com.sprint.deokhugam.user.application.required.UserRepository;
import com.sprint.deokhugam.user.domain.Email;
import com.sprint.deokhugam.user.domain.Password;
import com.sprint.deokhugam.user.domain.User;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCommandService implements UserCommand {

  private final UserRepository userRepository;
  private final PasswordHasher passwordHasher;

  @Override
  public UserDto save(UserRegisterRequest request) {
    Email email = Email.to(request.email());

    if (userRepository.existsByEmail(email)) {
      throw new BusinessException(UserErrorCode.DUPLICATE_EMAIL);
    }

    String encode = passwordHasher.hash(request.password());
    Password password = Password.to(encode);
    String nickname = request.nickname();
    User user = User.create(nickname, email, password);
    userRepository.save(user);
    return UserDto.to(
        user.getId(), user.getEmail().value(), user.getNickname(), user.getCreatedAt());
  }

  @Override
  public void delete(UUID userId, UUID headerId) {
    if (!userId.equals(headerId)) {
      throw new BusinessException(UserErrorCode.FORBIDDEN);
    }
    User user =
        userRepository
            .findById(userId)
            .filter(find -> !find.isDeleted())
            .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));

    user.softDelete();
  }

  @Override
  public UserDto update(UUID userId, UUID headerId, String nickname) {
    if (!userId.equals(headerId)) {
      throw new BusinessException(UserErrorCode.FORBIDDEN);
    }
    User user =
        userRepository
            .findById(userId)
            .filter(find -> !find.isDeleted())
            .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
    user.updateNickname(nickname);
    return UserDto.to(
        user.getId(), user.getEmail().value(), user.getNickname(), user.getCreatedAt());
  }
}
