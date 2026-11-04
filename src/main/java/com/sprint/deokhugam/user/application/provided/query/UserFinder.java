package com.sprint.deokhugam.user.application.provided.query;

import com.sprint.deokhugam.common.pagination.CursorPageResponse;
import com.sprint.deokhugam.user.application.dto.query.PowerUserSearchCond;
import com.sprint.deokhugam.user.application.dto.result.PowerUserResult;
import com.sprint.deokhugam.user.application.dto.result.UserResult;
import com.sprint.deokhugam.user.domain.User;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface UserFinder {

  User findByIdOrThrow(UUID userId);

  UserResult getUserDetail(UUID userId);

  List<UUID> findCleanupIds(Instant cutoff, int limit);

  List<UUID> findRankingTargetIds();

  CursorPageResponse<PowerUserResult> findPowerUsers(PowerUserSearchCond cond);
}
