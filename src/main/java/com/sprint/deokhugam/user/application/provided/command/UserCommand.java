package com.sprint.deokhugam.user.application.provided.command;

import com.sprint.deokhugam.user.application.dto.command.RegisterUserCommand;
import com.sprint.deokhugam.user.application.dto.result.UserResult;
import java.util.UUID;

public interface UserCommand {

  UserResult registerUser(RegisterUserCommand command);

  void withdraw(UUID userId, UUID headerId);

  void delete(UUID userId);

  UserResult update(UUID userId, UUID headerId, String nickname);
}
