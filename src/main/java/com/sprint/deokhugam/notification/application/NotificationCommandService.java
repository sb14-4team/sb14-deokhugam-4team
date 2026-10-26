package com.sprint.deokhugam.notification.application;

import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.notification.application.dto.NotificationDto;
import com.sprint.deokhugam.notification.application.provided.command.NotificationCommand;
import com.sprint.deokhugam.notification.application.required.NotificationPersistencePort;
import com.sprint.deokhugam.notification.domain.Notification;
import com.sprint.deokhugam.notification.domain.exception.NotificationErrorCode;
import com.sprint.deokhugam.review.domain.Review;
import com.sprint.deokhugam.user.domain.User;
import java.util.List;
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
  public NotificationDto createLike(Review review, User user, String actorNickname) {
    String content = review.getContent();
    Notification notification = Notification.createLike(user, review, content, actorNickname);
    Notification save = notificationPersistencePort.save(notification);
    NotificationDto notificationDto =
        NotificationDto.to(
            notification.getId(),
            user.getId(),
            review.getId(),
            content,
            notification.getMessage(),
            notification.isConfirmed(),
            notification.getCreatedAt(),
            notification.getUpdatedAt());
    return notificationDto;
  }

  @Override
  public NotificationDto createComment(Review review, User user, String actorNickname) {
    String content = review.getContent();
    Notification notification = Notification.createComment(user, review, content, actorNickname);
    Notification save = notificationPersistencePort.save(notification);
    NotificationDto notificationDto =
        NotificationDto.to(
            notification.getId(),
            user.getId(),
            review.getId(),
            content,
            notification.getMessage(),
            notification.isConfirmed(),
            notification.getCreatedAt(),
            notification.getUpdatedAt());
    return notificationDto;
  }

  @Override
  public NotificationDto confirm(UUID notificationId, UUID userId) {
    Notification find =
        notificationPersistencePort
            .findById(notificationId)
            .filter(notification -> !notification.isDeleted())
            .orElseThrow(() -> new BusinessException(NotificationErrorCode.NOT_FOUND));

    if (!find.getUser().getId().equals(userId)) {
      throw new BusinessException(NotificationErrorCode.FORBIDDEN);
    }

    find.confirm();
    return NotificationDto.to(
        find.getId(),
        find.getUser().getId(),
        find.getReview().getId(),
        find.getReviewContent(),
        find.getMessage(),
        find.isConfirmed(),
        find.getCreatedAt(),
        find.getUpdatedAt());
  }

  @Override
  public void confirmAll(UUID userId) {
    List<Notification> allByUser = notificationPersistencePort.findAllByUser(userId);
    /*
     *  시간날 때 리팩터링 예정.
     * */
    allByUser.forEach(Notification::confirm);
  }
}
