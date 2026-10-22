package com.sprint.deokhugam.comment.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record CommentCreateRequest(
    @NotNull UUID reviewId,
    @NotNull UUID userId,
    @NotBlank @Size(min = 1, max = 1000) String content) {}
