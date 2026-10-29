package com.sprint.deokhugam.review.application.provided.command;

import java.util.List;
import java.util.UUID;

public interface ReviewLikeCommand {
  // 사용자의 좋아요 물리삭제
  long deleteLikesByUserIds(List<UUID> userIds, List<UUID> reviewIds);
}
