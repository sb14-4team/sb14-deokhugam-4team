package com.sprint.deokhugam.user;

import com.sprint.deokhugam.review.application.provided.command.ReviewCommand;
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
  private final ReviewCommand reviewCommand;

  @Override
  public void cleanup(Instant cutoff) {
    List<UUID> targetIds = userFinder.findCleanupIds(cutoff, 20);
    for (UUID targetId : targetIds) {
      //      reviewCommand.deleteReview(targetId);
      userCommand.delete(targetId);
      //    유저 아이디로 삭제 메서드 요청해야함 지금은
    }
  }
}
