package com.sprint.deokhugam.user.application.provided.command;

import com.sprint.deokhugam.user.domain.ranking.ActivityEvent;
import java.time.LocalDate;
import java.util.UUID;

public interface UserActivityCommand {

  void apply(ActivityEvent event);

  void closeRanking(UUID userId, LocalDate date);
}
