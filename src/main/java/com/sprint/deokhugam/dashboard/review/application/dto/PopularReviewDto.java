package com.sprint.deokhugam.dashboard.review.application.dto;

import com.sprint.deokhugam.dashboard.review.domain.PopularReviewPeriod;
import java.time.Instant;
import java.util.UUID;

public record PopularReviewDto(
    UUID id,
    UUID reviewId,
    UUID bookId,
    String bookTitle,
    String bookThumbnailUrl,
    UUID userId,
    String userNickname,
    String reviewContent,
    Double reviewRating,
    PopularReviewPeriod period,
    Instant createdAt,
    Long rank,
    Double score,
    Long likeCount,
    Long commentCount) {}
