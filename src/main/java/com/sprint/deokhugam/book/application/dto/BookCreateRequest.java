package com.sprint.deokhugam.book.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record BookCreateRequest(
    @NotBlank(message = "제목은 필수입니다.") String title,
    @NotBlank(message = "저자는 필수입니다.") String author,
    @NotBlank(message = "소개는 필수입니다.") String description,
    @NotBlank(message = "출판사는 필수입니다.") String publisher,
    @NotNull(message = "출간일은 필수입니다.") LocalDate publishedDate,
    String isbn) {}
