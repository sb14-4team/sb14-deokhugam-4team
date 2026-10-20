package com.sprint.deokhugam.review.application.dto;

import java.util.UUID;

/*
 리뷰 생성 유스케이스를 사용하기 위한 개념 객체
*/
public record CreateReviewCommand(UUID userId, UUID bookId, int rating, String content) {}
