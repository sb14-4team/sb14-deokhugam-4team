package com.sprint.deokhugam.review.application.dto.res;

import java.util.UUID;

public record BookReviewStatsResponse(UUID bookId, long reviewCount, double averageRating) {}
