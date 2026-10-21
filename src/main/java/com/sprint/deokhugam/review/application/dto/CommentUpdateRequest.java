package com.sprint.deokhugam.review.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentUpdateRequest(@NotNull @NotBlank String Content) {}
