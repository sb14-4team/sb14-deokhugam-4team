package com.sprint.deokhugam.dashboard.review.application.dto;

import com.sprint.deokhugam.review.domain.Review;

public record ReviewScoreDto(Review review, double score, long likeCount, long commentCount) {}
