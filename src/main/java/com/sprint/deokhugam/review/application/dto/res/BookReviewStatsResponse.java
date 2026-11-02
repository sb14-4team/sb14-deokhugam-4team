package com.sprint.deokhugam.review.application.dto.res;

import com.sprint.deokhugam.book.domain.Book;

public record BookReviewStatsResponse(Book book, long reviewCount, double averageRating) {}
