package com.sprint.deokhugam.user.application.provided.query;

import com.sprint.deokhugam.user.application.dto.UserDto;
import com.sprint.deokhugam.user.domain.User;
import java.util.UUID;

public interface UserFinder {

  User findById(UUID userId);

  UserDto getById(UUID userId);
}
