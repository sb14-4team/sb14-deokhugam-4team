package com.sprint.deokhugam.book.application;

import com.sprint.deokhugam.book.application.provided.command.BookReviewStatsCommand;
import com.sprint.deokhugam.book.application.required.BookRepositoryPort;
import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.domain.book.exception.BookErrorCode;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 리뷰 도메인이 부르는 쪽이라 리뷰 트랜잭션에 같이 묶인다 (실패하면 리뷰 변경도 함께 롤백)
@Service
@Transactional
@RequiredArgsConstructor
public class BookReviewStatsService implements BookReviewStatsCommand {

  private final BookRepositoryPort bookRepositoryPort;

  @Override
  public void addReview(UUID bookId, int rating) {
    findBook(bookId).addReview(rating);
  }

  @Override
  public void removeReview(UUID bookId, int rating) {
    findBook(bookId).removeReview(rating);
  }

  @Override
  public void changeReviewRating(UUID bookId, int oldRating, int newRating) {
    findBook(bookId).changeReviewScore(oldRating, newRating);
  }

  // 논리 삭제된 도서에 달린 리뷰가 바뀌어도 집계는 맞아야 하니 삭제 포함으로 찾는다
  private Book findBook(UUID bookId) {
    return bookRepositoryPort
        .findById(bookId)
        .orElseThrow(() -> new BusinessException(BookErrorCode.NOT_FOUND, "bookId=" + bookId));
  }
}
