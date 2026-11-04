package com.sprint.deokhugam.book.application.provided.command;

import com.sprint.deokhugam.book.domain.Book;

// 리뷰 도메인이 들고 있는 Book을 그대로 넘긴다. 리뷰 트랜잭션 안에서 불러야 변경 감지로 저장된다
public interface BookReviewStatsCommand {

  // 리뷰가 새로 달렸을 때: 리뷰 수 +1, 평점 다시 계산
  void addReview(Book book, int rating);

  // 리뷰가 지워졌을 때: 리뷰 수 -1, 평점 다시 계산
  void removeReview(Book book, int rating);

  // 리뷰 점수가 바뀌었을 때: 리뷰 수 그대로, 평점만 다시 계산
  void changeReviewRating(Book book, int oldRating, int newRating);
}
