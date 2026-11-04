package com.sprint.deokhugam.user.adapter.dto.req;

import com.sprint.deokhugam.user.application.dto.query.LoginUserQuery;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserLoginRequest(
    @NotBlank(message = "이메일은 필수입니다.") @Email(message = "이메일 형식을 확인해주세요.") String email,
    @NotBlank(message = "비밀번호는 필수입니다.") String password) {

  public LoginUserQuery toQuery() {
    return new LoginUserQuery(email, password);
  }
}
