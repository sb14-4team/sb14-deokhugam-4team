package com.sprint.deokhugam.notification.application.provided.command;

import com.sprint.deokhugam.notification.application.dto.NotificationDto;
import java.util.UUID;

public interface NotificationCommand {

  // 좋아요 알림 생성(어떤 리뷰에, 누구한테, 좋아요 누른사람 닉네임)
  void createLike(UUID reviewId, UUID userId, String actorNickname);

  // 댓글 알림 생성 (어떤 리뷰에, 누구한테, 댓글 단 사람 닉네임)
  void createComment(UUID reviewId, UUID userId, String actorNickname);

  // 알림 하나를 확인 처리 (본인인가 확인)
  NotificationDto confirm(UUID notificationId, UUID userId);

  // 유저가 알림을 전부 확인 처리
  void confirmAll(UUID userId);
}
