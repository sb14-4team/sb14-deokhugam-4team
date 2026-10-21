package com.sprint.deokhugam.user.adapter.web;

import com.sprint.deokhugam.user.application.dto.UserDto;
import com.sprint.deokhugam.user.application.dto.UserRegisterRequest;
import com.sprint.deokhugam.user.application.provided.command.UserCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserApiController {

  private final UserCommand userCommand;

  @PostMapping
  public ResponseEntity<UserDto> register(@Valid @RequestBody UserRegisterRequest request) {
    UserDto userDto = userCommand.save(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
  }
}
