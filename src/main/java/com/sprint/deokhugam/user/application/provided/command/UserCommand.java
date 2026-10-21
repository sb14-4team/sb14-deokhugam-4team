package com.sprint.deokhugam.user.application.provided.command;

import com.sprint.deokhugam.user.application.dto.UserDto;
import com.sprint.deokhugam.user.application.dto.UserRegisterRequest;
import java.util.UUID;

public interface UserCommand {

  UserDto save(UserRegisterRequest request);

  void delete(UUID userId, UUID headerId);

  UserDto update(UUID userId, UUID headerId, String nickname);
}
