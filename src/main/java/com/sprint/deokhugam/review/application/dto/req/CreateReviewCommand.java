package com.sprint.deokhugam.review.application.dto.req;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CreateReviewCommand(
    @NotNull UUID userId,
    @NotNull UUID bookId,
    @Min(1) @Max(5) int rating,
    @NotNull @Size(max = 200) String content) {}
