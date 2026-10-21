package com.sprint.deokhugam.user.application.dto;

import com.sprint.deokhugam.user.domain.Email;
import com.sprint.deokhugam.user.domain.Password;

public record UserLoginRequest(Email email, Password password) {}
