package com.sprint.deokhugam.user.domain;


import java.util.regex.Pattern;

public record Password(
    String password
) {

  private static final Pattern FORMAT =
      Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$");

  public Password {
    if (!FORMAT.matcher(password).matches()) {
      throw new RuntimeException();
    }
    if (password == null || password.isBlank()) {
      throw new RuntimeException();
    }
  }
}
