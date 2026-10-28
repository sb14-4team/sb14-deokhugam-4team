package com.sprint.deokhugam.book.application;

import com.sprint.deokhugam.book.application.provided.command.BookReviewStatsCommand;
import com.sprint.deokhugam.book.domain.Book;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 리뷰 도메인이 부르는 쪽이라 리뷰 트랜잭션에 같이 묶인다 (실패하면 리뷰 변경도 함께 롤백)
@Service
@Transactional
public class BookReviewStatsService implements BookReviewStatsCommand {

  @Override
  public void addReview(Book book, int rating) {
    book.addReview(rating);
  }

  @Override
  public void removeReview(Book book, int rating) {
    book.removeReview(rating);
  }

  @Override
  public void changeReviewRating(Book book, int oldRating, int newRating) {
    book.changeReviewScore(oldRating, newRating);
  }
}
