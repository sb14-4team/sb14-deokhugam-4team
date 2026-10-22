package com.sprint.deokhugam.user;

import com.sprint.deokhugam.user.application.provided.command.UserCleaner;
import com.sprint.deokhugam.user.application.provided.command.UserCommand;
import com.sprint.deokhugam.user.application.provided.query.UserFinder;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserApplicationService implements UserCleaner {

  private final UserFinder userFinder;
  private final UserCommand userCommand;

  @Override
  public void cleanup(Instant cutoff) {
    List<UUID> targetIds = userFinder.findCleanupIds(cutoff, 20);
    for (UUID targetId : targetIds) {
      userCommand.delete(targetId);
    }
  }
}
