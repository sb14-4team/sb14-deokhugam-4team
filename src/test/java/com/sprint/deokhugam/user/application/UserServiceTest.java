package com.sprint.deokhugam.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.user.adapter.dto.req.UserLoginRequest;
import com.sprint.deokhugam.user.adapter.dto.req.UserRegisterRequest;
import com.sprint.deokhugam.user.adapter.dto.res.UserResponse;
import com.sprint.deokhugam.user.application.dto.command.RegisterUserCommand;
import com.sprint.deokhugam.user.application.dto.query.LoginUserQuery;
import com.sprint.deokhugam.user.application.dto.result.UserResult;
import com.sprint.deokhugam.user.application.provided.command.UserCommand;
import com.sprint.deokhugam.user.application.provided.query.UserFinder;
import com.sprint.deokhugam.user.application.provided.query.UserLogin;
import com.sprint.deokhugam.user.domain.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Transactional
@Slf4j
class UserServiceTest {

  private final UserFinder userFinder;
  private final UserCommand userCommand;
  private final UserLogin userLogin;
  private final EntityManager em;
  private UserResponse user;

  @BeforeEach
  void given() {
    UserRegisterRequest request = new UserRegisterRequest("tester@test.com", "test1", "password!");
    RegisterUserCommand command = request.toCommand();
    UserResult result = userCommand.registerUser(command);
    user = UserResponse.from(result);

    em.flush();
    em.clear();
  }

  @Test
  void register() {
    UserRegisterRequest request = new UserRegisterRequest("tester@test.com", "test1", "password!");
    UserRegisterRequest request2 =
        new UserRegisterRequest("tester@test.com", "test12", "password!");
    RegisterUserCommand command2 = request2.toCommand();
    assertThatThrownBy(() -> userCommand.registerUser(command2))
        .isInstanceOf(BusinessException.class);
  }

  @Test
  void findById() {
    // when
    User finderById = userFinder.findByIdOrThrow(user.id());

    // then
    assertThat(finderById).isNotNull();
    assertThat(finderById.getNickname()).isEqualTo("test1");
    assertThat(finderById.getEmail().value()).isEqualTo("tester@test.com");
    log.info("nickname= {}", finderById.getNickname());
    log.info("email= {}", finderById.getEmail());
    log.info("email.value()= {}", finderById.getEmail().value());
    log.info("pwd= {}", finderById.getPassword());
    log.info("pwd.password()= {}", finderById.getPassword().password());
  }

  @Test
  void login() {
    // when
    UserLoginRequest request = new UserLoginRequest("tester@test.com", "password!");
    LoginUserQuery query = request.toQuery();
    UserResult result = userLogin.login(query);
    UserResponse userResponse = UserResponse.from(result);
    // then
    assertThat(userResponse.id()).isEqualTo(user.id());
  }
}
