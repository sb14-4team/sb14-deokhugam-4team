package com.sprint.deokhugam.user.application.provided.command;

import java.time.Instant;

public interface UserCleaner {

  void cleanup(Instant cutoff);
}
