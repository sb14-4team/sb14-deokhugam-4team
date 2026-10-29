package com.sprint.deokhugam.book.application.dto;

import com.sprint.deokhugam.book.domain.Book;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record BookDto(
    UUID id,
    String title,
    String author,
    String description,
    String publisher,
    LocalDate publishedDate,
    String isbn,
    String thumbnailUrl,
    int reviewCount,
    double rating,
    Instant createdAt,
    Instant updatedAt) {

  public static BookDto from(Book book) {
    return new BookDto(
        book.getId(),
        book.getTitle(),
        book.getAuthor(),
        book.getDescription(),
        book.getPublisher(),
        book.getPublishedDate(),
        book.getIsbn() == null ? null : book.getIsbn().value(),
        book.getThumbnailUrl(),
        book.getReviewCount(),
        book.getRating(),
        book.getCreatedAt(),
        book.getUpdatedAt());
  }
}
