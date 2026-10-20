package com.sprint.deokhugam.user.application.dto;

import com.sprint.deokhugam.user.domain.Email;
import java.time.Instant;
import java.util.UUID;

public record UserDto(
    UUID id,
    Email email,
    String nickname,
    Instant createdAt
) {

  public static UserDto to(UUID id, Email email, String nickname, Instant createdAt) {
    return new UserDto(id, email, nickname, createdAt);
  }
}
