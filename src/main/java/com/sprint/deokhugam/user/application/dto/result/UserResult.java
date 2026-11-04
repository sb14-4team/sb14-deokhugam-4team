package com.sprint.deokhugam.user.application.dto.result;

import com.sprint.deokhugam.user.domain.User;
import java.time.Instant;
import java.util.UUID;

public record UserResult(UUID id, String email, String nickname, Instant createdAt) {

  public static UserResult from(User user) {
    return new UserResult(
        user.getId(), user.getEmail().value(), user.getNickname(), user.getCreatedAt());
  }
}
