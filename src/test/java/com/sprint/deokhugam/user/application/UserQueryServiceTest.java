package com.sprint.deokhugam.user.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.deokhugam.user.application.dto.UserDto;
import com.sprint.deokhugam.user.application.dto.UserLoginRequest;
import com.sprint.deokhugam.user.application.dto.UserRegisterRequest;
import com.sprint.deokhugam.user.application.provided.command.UserCommand;
import com.sprint.deokhugam.user.application.provided.query.UserFinder;
import com.sprint.deokhugam.user.application.provided.query.UserLogin;
import com.sprint.deokhugam.user.domain.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Transactional
class UserQueryServiceTest {

  private final UserFinder userFinder;
  private final UserCommand userCommand;
  private final UserLogin userLogin;
  private final EntityManager em;
  private UserDto user;

  @BeforeEach
  void given() {
    UserRegisterRequest request = new UserRegisterRequest("tester@test.com", "test1", "password!");
    user = userCommand.save(request);
    em.flush();
    em.clear();
  }

  @Test
  void findById() {
    // when
    User finderById = userFinder.findById(user.id());

    // then
    assertThat(finderById).isNotNull();
    assertThat(finderById.getNickname()).isEqualTo("test1");
    assertThat(finderById.getEmail().value()).isEqualTo("tester@test.com");
  }

  @Test
  void login() {
    // when
    UserLoginRequest request = new UserLoginRequest("tester@test.com", "password!");
    UserDto userDto = userLogin.login(request);
    // then
    assertThat(userDto.id()).isEqualTo(user.id());
  }
}
