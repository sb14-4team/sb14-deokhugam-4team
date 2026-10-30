package com.sprint.deokhugam.book;

import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.review.application.ReviewCleanupUseCase;
import java.util.ArrayList;
import java.util.List;

// 도서 테스트에서 리뷰 도메인 대신 쓴다. 물리 삭제 때 어떤 도서가 넘어왔는지만 기록한다
public class FakeReviewCommand implements ReviewCleanupUseCase {

  private final List<Book> cleanedBooks = new ArrayList<>();

  public List<Book> cleanedBooks() {
    return cleanedBooks;
  }

  @Override
  public long deleteReviewsWithRelatedDataByBook(Book book) {
    cleanedBooks.add(book);
    return 0L;
  }
}
