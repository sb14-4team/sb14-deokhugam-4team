package com.sprint.deokhugam.notification.application.required;

import com.sprint.deokhugam.notification.domain.Notification;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationPersistencePort {

  // 알림을 저장해줌
  Notification save(Notification notification);

  // ID로 알림 단건 조회
  Optional<Notification> findById(UUID notificationId);

  // 수신자의 알림 목록 조회
  List<Notification> findAllByUserId(UUID userId, Instant after, int limit);

  // 이전 페이지에서 마지막 받은 알림의 createdAt ( 첫 페이지면 null)
  // limit = 한번에 주는 갯수

  // 수신자의 미확인 알림 전부 확인 처리
  int confirmAllByUserId(UUID userId);
}
