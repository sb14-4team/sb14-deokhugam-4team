package com.sprint.deokhugam.user.adapter.scheduler;

import com.sprint.deokhugam.user.application.provided.command.UserCleaner;
import java.time.Duration;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserCleanupScheduler {

  private final UserCleaner userCleaner;

  @Scheduled(cron = "0 */5 * * * *", zone = "Asia/Seoul")
  public void cleanup() {
    Instant cutoff = Instant.now().minus(Duration.ofMinutes(5));
    userCleaner.cleanup(cutoff);
  }
}
