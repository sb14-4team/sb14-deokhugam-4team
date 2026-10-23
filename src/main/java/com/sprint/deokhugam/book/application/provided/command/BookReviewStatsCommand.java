package com.sprint.deokhugam.book.application.provided.command;

import java.util.UUID;

public interface BookReviewStatsCommand {

  // 리뷰가 새로 달렸을 때: 리뷰 수 +1, 평점 다시 계산
  void addReview(UUID bookId, int rating);

  // 리뷰가 지워졌을 때: 리뷰 수 -1, 평점 다시 계산
  void removeReview(UUID bookId, int rating);

  // 리뷰 점수가 바뀌었을 때: 리뷰 수 그대로, 평점만 다시 계산
  void changeReviewRating(UUID bookId, int oldRating, int newRating);
}
