package com.sprint.deokhugam.user.application.provided.query;

import com.sprint.deokhugam.user.application.dto.query.LoginUserQuery;
import com.sprint.deokhugam.user.application.dto.result.UserResult;

public interface UserLogin {

  UserResult login(LoginUserQuery query);
}
