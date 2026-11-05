package com.sprint.deokhugam.user.adapter.dto.res;

import com.sprint.deokhugam.user.application.dto.result.UserResult;
import java.time.Instant;
import java.util.UUID;

public record UserResponse(UUID id, String email, String nickname, Instant createdAt) {

  public static UserResponse from(UserResult result) {
    return new UserResponse(result.id(), result.email(), result.nickname(), result.createdAt());
  }
}
