package com.sprint.deokhugam.review.application.provided.command;

import java.util.UUID;

public interface ReviewLikeCommand {
  // 탈퇴 사용자의 좋아요 삭제 - 삭제 시 남는 리뷰에 삭제 반영
  long deleteLikesByUserId(UUID userId);
}
