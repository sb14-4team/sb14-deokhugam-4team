package com.sprint.deokhugam.notification.adapter.persistence;

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

  @Override
  public Notification save(Notification notification) {
    return notificationJpaRepository.save(notification);
  }

  @Override
  public Optional<Notification> findById(UUID notificationId) {
    return notificationJpaRepository.findById(notificationId);
  }

  @Override
  public List<Notification> findAllByUser(UUID userId, Instant after, int limit) {
    return List.of();
  }

  @Override
  public int confirmAllByUserId(UUID userId) {
    return 0;
  }
}
