package com.sprint.deokhugam.notification.application.provided.command;

import java.time.Instant;

public interface NotificationCleaner {

  void deleteConfirmed(Instant cutoff);
}
