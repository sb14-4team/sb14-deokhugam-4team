/*package com.sprint.deokhugam.notification.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NotificationTest {

  private final UUID receiverId = UUID.randomUUID();
  private final UUID reviewId = UUID.randomUUID();

  private Notification createLike() {
    return Notification.createLike(receiverId, reviewId, "이 책을 읽고 기분이 안좋아졌어요!", "12");
  }

  @Test
  @DisplayName("좋아요 알림 = 사용자명, 리뷰 정보, 닉네임")
  void createLikeNotification() {
    Notification notification = createLike();

    assertThat(notification.getType()).isEqualTo(NotificationType.LIKE);
    assertThat(notification.getUserId()).isEqualTo(receiverId);
    assertThat(notification.getReviewId()).isEqualTo(reviewId);
    assertThat(notification.getReviewContent()).isEqualTo("이 책을 읽고 기분이 안좋아졌어요!");
    assertThat(notification.getMessage()).isEqualTo("[12]님이 나의 리뷰를 좋아합니다.");
  }
}
*/
