package com.sprint.deokhugam.notification.application;

import com.sprint.deokhugam.notification.application.dto.NotificationDto;
import com.sprint.deokhugam.notification.application.provided.command.NotificationCommand;
import com.sprint.deokhugam.notification.application.required.NotificationPersistencePort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationCommandService implements NotificationCommand {

  private final NotificationPersistencePort notificationPersistencePort;

  @Override
  public void createLike(UUID reviewId, UUID userId, String actorNickname) {}

  @Override
  public void createComment(UUID reviewId, UUID userId, String actorNickname) {}

  @Override
  public NotificationDto confirm(UUID notificationId, UUID userId) {
    return null;
  }

  @Override
  public void confirmAll(UUID userId) {}
}
