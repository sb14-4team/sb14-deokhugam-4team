package com.sprint.deokhugam.review.application.dto.req;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateReviewRequest(
    @NotNull @Size(max = 200) String content, @NotNull @Min(1) @Max(5) Integer rating) {}
