package com.sprint.deokhugam.comment.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentUpdateRequest(@NotBlank @Size(max = 1000) String content) {}
