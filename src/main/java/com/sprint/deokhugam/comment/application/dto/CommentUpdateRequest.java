package com.sprint.deokhugam.comment.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommentUpdateRequest(@NotNull @NotBlank String content) {}
