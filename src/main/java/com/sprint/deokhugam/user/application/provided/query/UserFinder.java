package com.sprint.deokhugam.user.application.provided.query;

import com.sprint.deokhugam.user.application.dto.UserDto;
import com.sprint.deokhugam.user.domain.User;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface UserFinder {

  User findById(UUID userId);

  UserDto getById(UUID userId);

  List<UUID> findCleanupIds(Instant cutoff, int limit);
}
