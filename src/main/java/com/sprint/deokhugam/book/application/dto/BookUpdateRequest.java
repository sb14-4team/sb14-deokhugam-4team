package com.sprint.deokhugam.book.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

// 수정 요청엔 isbn이 없다. 요청 JSON에 isbn을 넣어도 무시된다 → ISBN은 바꿀 수 없다
public record BookUpdateRequest(
    @NotBlank(message = "제목은 필수입니다.") String title,
    @NotBlank(message = "저자는 필수입니다.") String author,
    @NotBlank(message = "소개는 필수입니다.") String description,
    @NotBlank(message = "출판사는 필수입니다.") String publisher,
    @NotNull(message = "출간일은 필수입니다.") LocalDate publishedDate) {}
