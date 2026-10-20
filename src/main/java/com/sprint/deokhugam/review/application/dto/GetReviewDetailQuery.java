package com.sprint.deokhugam.review.application.dto;

import java.util.UUID;

/*
 읽기용
*/
public record GetReviewDetailQuery(UUID reviewId, UUID requestUserId) {}
