package com.sprint.deokhugam.user.adapter.web;

import com.sprint.deokhugam.user.application.dto.UserDto;
import com.sprint.deokhugam.user.application.dto.UserLoginRequest;
import com.sprint.deokhugam.user.application.dto.UserRegisterRequest;
import com.sprint.deokhugam.user.application.provided.command.UserCommand;
import com.sprint.deokhugam.user.application.provided.query.UserFinder;
import com.sprint.deokhugam.user.application.provided.query.UserLogin;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserApiController {

  private final UserCommand userCommand;
  private final UserLogin userLogin;
  private final UserFinder userFinder;

  @PostMapping
  public ResponseEntity<UserDto> register(@Valid @RequestBody UserRegisterRequest request) {
    UserDto userDto = userCommand.save(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
  }

  @PostMapping("/login")
  public ResponseEntity<UserDto> login(@Valid @RequestBody UserLoginRequest request) {
    UserDto userDto = userLogin.login(request);
    return ResponseEntity.ok(userDto);
  }

  @GetMapping("/{userId}")
  public ResponseEntity<UserDto> getUser(@PathVariable UUID userId) {
    UserDto userDto = userFinder.getById(userId);
    return ResponseEntity.ok(userDto);
  }
}
