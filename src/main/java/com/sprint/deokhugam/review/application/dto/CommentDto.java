package com.sprint.deokhugam.review.application.dto;

import java.time.Instant;
import java.util.UUID;

public record CommentDto(
    UUID id,
    UUID reviewId,
    UUID userId,
    String userNickname,
    String content,
    Instant createAt,
    Instant updateAt) {}
