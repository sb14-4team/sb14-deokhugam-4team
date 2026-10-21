package com.sprint.deokhugam.user.application.provided.command;

import com.sprint.deokhugam.user.application.dto.UserDto;
import com.sprint.deokhugam.user.application.dto.UserRegisterRequest;
import com.sprint.deokhugam.user.domain.User;

public interface UserCommand {

  UserDto save(UserRegisterRequest request);

  void delete(User user);

  User update(User user, String nickname);
}
