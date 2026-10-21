package com.sprint.deokhugam.review.application.dto.req;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/*
 리뷰 생성 유스케이스를 사용하기 위한 개념 객체
*/
@NotNull
public record CreateReviewCommand(
    UUID userId, UUID bookId, @Min(1) @Max(5) int rating, @Min(1) @Max(200) String content) {}
