package com.sprint.deokhugam.user.application.provided.query;

import com.sprint.deokhugam.user.application.dto.UserDto;
import com.sprint.deokhugam.user.application.dto.UserLoginRequest;

public interface UserLogin {

  UserDto login(UserLoginRequest request);
}
