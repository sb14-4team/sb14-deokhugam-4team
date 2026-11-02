package com.sprint.deokhugam.dashboard.review.application.dto;

import java.util.UUID;

// 집계 기간 동안 한 리뷰가 받은 좋아요 수, 댓글 수
public record ReviewActivity(UUID reviewId, Long likeCount, Long commentCount) {}
