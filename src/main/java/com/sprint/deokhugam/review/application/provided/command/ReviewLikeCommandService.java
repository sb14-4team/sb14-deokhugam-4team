package com.sprint.deokhugam.review.application.provided.command;

import com.sprint.deokhugam.review.application.required.ReviewLikeRepositoryPort;
import com.sprint.deokhugam.review.application.required.ReviewRepositoryPort;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReviewLikeCommandService implements ReviewLikeCommand {
  private final ReviewLikeRepositoryPort reviewLikeRepositoryPort;
  private final ReviewRepositoryPort reviewRepositoryPort;

  @Override
  @Transactional
  public long deleteLikesByUserIds(List<UUID> userIds, List<UUID> reviewIds) {
    if (userIds.isEmpty() && reviewIds.isEmpty()) return 0L;

    reviewRepositoryPort.decreaseLikeCountsByUserIds(userIds); // 좋아요 수 감소 반영
    return reviewLikeRepositoryPort.deleteAllByUserIdsOrReviewIds(userIds, reviewIds);
  }
}
