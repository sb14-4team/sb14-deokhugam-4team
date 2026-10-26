package com.sprint.deokhugam.notification.adapter.persistence;

import com.sprint.deokhugam.notification.application.dto.NotificationSearchCond;
import com.sprint.deokhugam.notification.application.required.NotificationPersistencePort;
import com.sprint.deokhugam.notification.domain.Notification;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NotificationJpaRepositoryAdaptor implements NotificationPersistencePort {

  private final NotificationJpaRepository notificationJpaRepository;
  private final NotificationQRepository notificationQRepository;

  @Override
  public Notification save(Notification notification) {
    return notificationJpaRepository.save(notification);
  }

  @Override
  public Optional<Notification> findById(UUID notificationId) {
    return notificationJpaRepository.findById(notificationId);
  }

  @Override
  public List<Notification> findAll(NotificationSearchCond cond) {
    return notificationQRepository.findAll(cond);
  }

  @Override
  public List<UUID> findCleanupIds(Instant cutoff, int limit) {
    return notificationQRepository.findCleanupIds(cutoff, limit);
  }

  @Override
  public long countByUser(UUID userId) {
    return notificationQRepository.countByUser(userId);
  }

  @Override
  public List<Notification> findAllByUser(UUID userId) {
    return notificationJpaRepository.findAllByUser_Id(userId);
  }
}
