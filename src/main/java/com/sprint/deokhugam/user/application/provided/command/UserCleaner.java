package com.sprint.deokhugam.user.application.provided.command;

import java.util.UUID;

public interface UserCleaner {

  void cleanup(UUID targetId);
}
