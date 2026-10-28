package com.sprint.deokhugam.review.application.provided.command;

import java.util.List;
import java.util.UUID;

public interface ReviewLikeCommand {
  long deleteLikesByUserIds(List<UUID> userIds); // 탈퇴 사용자가 누른 좋아요
}
