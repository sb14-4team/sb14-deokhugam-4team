package com.sprint.deokhugam.notification.application.provided.query;

import com.sprint.deokhugam.common.pagination.CursorPageResponse;
import com.sprint.deokhugam.notification.application.dto.NotificationDto;
import com.sprint.deokhugam.notification.application.dto.NotificationSearchCond;
import com.sprint.deokhugam.notification.domain.Notification;
import java.util.UUID;

public interface NotificationFinder {

  Notification findById(UUID uuid);

  CursorPageResponse<NotificationDto> findAllByUser(NotificationSearchCond cond);

  long countByUser(UUID userId);
}
