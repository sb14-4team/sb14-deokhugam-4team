package com.sprint.deokhugam.user.domain;

import java.util.regex.Pattern;

public record Email(
    String value
) {

  private static final Pattern FORMAT =
      Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

  public Email {
    if (value == null || value.isBlank()) {
      throw new RuntimeException();
    }

    if (!FORMAT.matcher(value).matches()) {
      throw new RuntimeException();
    }

//    if (value.length() > 100)
  }

}
