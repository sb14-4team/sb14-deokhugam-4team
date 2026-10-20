package com.sprint.deokhugam.user.domain;

import com.sprint.deokhugam.common.entity.BaseSoftDeletableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseSoftDeletableEntity {

  @Column(name = "name", nullable = false, length = 20)
  private String nickname;

  @Column(name = "email", nullable = false, unique = true)
  private Email email;

  @Column(name = "password", nullable = false)
  private Password password;

  public User(String nickname, Email email, Password password) {
    this.nickname = nickname;
    this.email = email;
    this.password = password;
  }

  public static User craete(String nickname, Email email, Password password) {
    return new User(nickname, email, password);
  }
}

