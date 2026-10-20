package com.sprint.deokhugam.user.application.required;

import com.sprint.deokhugam.user.domain.User;

public interface UserRepository {

  public User save(User user);
}
