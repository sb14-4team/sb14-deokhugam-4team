package com.sprint.deokhugam.user.domain;

public record Password(String password) {

  public Password {
    if (password == null || password.isBlank()) {
      throw new RuntimeException();
    }
  }

  public static Password to(String value) {
    return new Password(value);
  }
}
