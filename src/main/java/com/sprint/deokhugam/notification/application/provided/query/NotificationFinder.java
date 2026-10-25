package com.sprint.deokhugam.notification.application.provided.query;

import com.sprint.deokhugam.notification.application.dto.NotificationDto;
import com.sprint.deokhugam.notification.domain.Notification;
import java.util.List;
import java.util.UUID;

public interface NotificationFinder {

  Notification findById(UUID uuid);

  List<NotificationDto> findAllByUser(UUID userId);
}
