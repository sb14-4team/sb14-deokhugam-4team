package com.sprint.deokhugam.notification.application;

import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.notification.application.dto.NotificationDto;
import com.sprint.deokhugam.notification.application.provided.query.NotificationFinder;
import com.sprint.deokhugam.notification.application.required.NotificationPersistencePort;
import com.sprint.deokhugam.notification.domain.Notification;
import com.sprint.deokhugam.notification.domain.exception.NotificationErrorCode;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationQueryService implements NotificationFinder {

  private final NotificationPersistencePort notificationPersistencePort;

  @Override
  public Notification findById(UUID uuid) {
    return notificationPersistencePort
        .findById(uuid)
        .filter(notification -> !notification.isDeleted())
        .orElseThrow(() -> new BusinessException(NotificationErrorCode.NOT_FOUND));
  }

  @Override
  public List<NotificationDto> findAllByUser(UUID userId) {
    return List.of();
  }
}
