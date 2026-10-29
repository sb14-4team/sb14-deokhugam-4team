package com.sprint.deokhugam.notification.application.required;

import com.sprint.deokhugam.notification.application.dto.NotificationSearchCond;
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

  List<Notification> findAll(NotificationSearchCond cond);

  List<UUID> findCleanupIds(Instant cutoff, int limit);

  public void deleteConfirmed(Instant cutoff);

  long countByUser(UUID userId);

  public List<Notification> findAllByUser(UUID userId);

  void deleteByUser(UUID userId);

  void deleteByReview(UUID reviewId);

  void deleteByReviewIds(List<UUID> reviewIds);
}
