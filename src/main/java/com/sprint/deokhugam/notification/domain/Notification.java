package com.sprint.deokhugam.notification.domain;

import com.sprint.deokhugam.common.entity.BaseSoftDeletableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "notifications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseSoftDeletableEntity {

  // 알림을 받는사람
  @JoinColumn(name = "user_id")
  private UUID userId;

  // 알림 대상 리뷰 (알림을 누르면 이동할 리뷰)
  @JoinColumn(name = "review_id")
  private UUID reviewId;

  // 알림 생성 시점의 리뷰 내용 (리뷰가 수정돼도 그대로 유지)
  @Column(name = "review_content")
  private String reviewContent;

  // 알림 종류
  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false) // 필수조건
  private NotificationType type;

  // 알림 문구 (닉네임 고정, 추후 닉네임 변경해도 동일)
  @Column(name = "message", nullable = false)
  private String message;

  // 읽었어?
  @Column(name = "confirmed")
  private boolean confirmed;

  // 읽은 시간 ( 추후 읽은 후 1주일 뒤 삭제 구현 예정)
  @Column(name = "confirmed_at")
  private Instant confirmedAt;

  private Notification(
      UUID userId, UUID reviewId, String reviewContent, NotificationType type, Object... args) {
    this.userId = userId;
    this.reviewId = reviewId;
    this.message = type.format(args);
    this.reviewContent = reviewContent;
    this.type = type;
    this.confirmed = false;
  }

  public static Notification createLike(
      UUID userId, UUID reviewId, String reviewContent, String actorNickname) {
    return new Notification(userId, reviewId, reviewContent, NotificationType.LIKE, actorNickname);
  }

  public static Notification createComment(
      UUID userId, UUID reviewId, String reviewContent, String actorNickname) {
    return new Notification(
        userId, reviewId, reviewContent, NotificationType.COMMENT, actorNickname);
  }

  public void confirm() {
    if (confirmed) {
      return;
    }
    this.confirmed = true;
    this.confirmedAt = Instant.now();
  }
}
