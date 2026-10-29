package com.sprint.deokhugam.review.application.provided.command;

import com.sprint.deokhugam.review.application.required.ReviewLikePersistencePort;
import com.sprint.deokhugam.review.application.required.ReviewPersistencePort;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReviewLikeCommandService implements ReviewLikeCommand {
  private final ReviewLikePersistencePort reviewLikePersistencePort;
  private final ReviewPersistencePort reviewPersistencePort;

  @Override
  @Transactional
  public long deleteLikesByUserIds(List<UUID> userIds, List<UUID> reviewIds) {
    if (userIds.isEmpty() && reviewIds.isEmpty()) return 0L;

    reviewPersistencePort.decreaseLikeCountsByUserIds(userIds); // 좋아요 수 감소 반영
    return reviewLikePersistencePort.deleteAllByUserIdsOrReviewIds(userIds, reviewIds);
  }
}
