package com.sprint.deokhugam.book;

import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.review.application.provided.command.ReviewCommand;
import com.sprint.deokhugam.review.domain.Review;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// 도서 테스트에서 리뷰 도메인 대신 쓴다. 물리 삭제 때 어떤 도서가 넘어왔는지만 기록한다
public class FakeReviewCommand implements ReviewCommand {

  private final List<Book> cleanedBooks = new ArrayList<>();

  public List<Book> cleanedBooks() {
    return cleanedBooks;
  }

  @Override
  public Review createReview(Review review) {
    throw new UnsupportedOperationException("도서 테스트에서는 쓰지 않는다");
  }

  @Override
  public Review updateReview(UUID reviewId, UUID userId, String newContent, int newRating) {
    throw new UnsupportedOperationException("도서 테스트에서는 쓰지 않는다");
  }

  @Override
  public long deleteReviewByIds(List<UUID> reviewIds) {
    throw new UnsupportedOperationException("도서 테스트에서는 쓰지 않는다");
  }

  @Override
  public long deleteReviewsWithRelatedDataByBook(Book book) {
    cleanedBooks.add(book);
    return 0L;
  }
}
