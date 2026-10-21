package com.sprint.deokhugam.user.application.provided.query;

import com.sprint.deokhugam.user.domain.Email;
import com.sprint.deokhugam.user.domain.User;
import java.util.UUID;

public interface UserFinder {

  User findById(UUID userId);

  User findByEmail(Email email);
}
