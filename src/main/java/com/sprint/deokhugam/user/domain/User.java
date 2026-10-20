package com.sprint.deokhugam.user.domain;

import com.sprint.deokhugam.common.entity.BaseSoftDeletableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
  @Embedded
  private Email email;

  @Column(name = "password", nullable = false)
  @Embedded
  private Password password;

}
