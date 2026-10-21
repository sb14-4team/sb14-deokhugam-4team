package com.sprint.deokhugam.user.application.required;

import com.sprint.deokhugam.user.domain.Email;
import com.sprint.deokhugam.user.domain.User;

public interface UserRepository {

  User save(User user);

  boolean existsByEmail(Email email);
}
