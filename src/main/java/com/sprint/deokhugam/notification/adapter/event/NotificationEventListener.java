package com.sprint.deokhugam.notification.adapter.event;

import com.sprint.deokhugam.notification.NotificationApplicationService;
import com.sprint.deokhugam.notification.domain.NotifyEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

  private final NotificationApplicationService notificationApplicationService;

  @EventListener
  public void handle(NotifyEvent event) {
    switch (event) {
      case NotifyEvent.Comment e ->
          notificationApplicationService.notifyComment(e.reviewId(), e.actorName());
      case NotifyEvent.Like e ->
          notificationApplicationService.notifyLike(e.reviewId(), e.actorName());
    }
  }
}
