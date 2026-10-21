package com.sprint.deokhugam.user.domain;

import com.sprint.deokhugam.common.entity.BaseSoftDeletableEntity;
import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.domain.user.exception.UserErrorCode;
import com.sprint.deokhugam.user.adapter.persistence.EmailConverter;
import com.sprint.deokhugam.user.adapter.persistence.PasswordConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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

  @Convert(converter = EmailConverter.class)
  @Column(name = "email", nullable = false, unique = true)
  private Email email;

  @Convert(converter = PasswordConverter.class)
  @Column(name = "password", nullable = false)
  private Password password;

  private User(String nickname, Email email, Password password) {
    this.nickname = nickname;
    this.email = email;
    this.password = password;
  }

  public void updateNickname(String nickname) {
    if (this.nickname.equals(nickname)) {
      throw new BusinessException(UserErrorCode.DUPLICATE_NICKNAME);
    }
    this.nickname = nickname;
  }

  public static User create(String nickname, Email email, Password password) {
    return new User(nickname, email, password);
  }
}
