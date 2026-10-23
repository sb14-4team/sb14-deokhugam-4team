package com.sprint.deokhugam.notification.application.dto;

import java.time.Instant;
import java.util.UUID;

public record NotificationDto(
    UUID id,
    UUID userId,
    UUID reviewId,
    String reviewContent,
    String message,
    boolean confirmed,
    Instant createdAt,
    Instant updatedAt) {}
