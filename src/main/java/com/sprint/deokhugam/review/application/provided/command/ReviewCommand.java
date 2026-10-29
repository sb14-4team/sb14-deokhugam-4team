package com.sprint.deokhugam.review.application.provided.command;

import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.review.domain.Review;
import java.util.List;
import java.util.UUID;

public interface ReviewCommand {
  Review createReview(Review review);

  Review updateReview(UUID reviewId, UUID userId, String newContent, int newRating);

  /*
   배치 스케줄러에서 호출하는 메서드
   요청인자 : 삭제할 리뷰 아이디 목록
   리턴값 : long 삭제 완료된 레코드 수 - 쿼리 dsl delete 리턴값 long 이라 long 으로 리턴
   - 주의 : 연관된 댓글/좋아요가 먼저 정리되어 있어야 함
  */
  long deleteReviewByIds(List<UUID> reviewIds);

  /*
   도서 물리 삭제시에 호출하는 메서드
   요청인자 : 삭제할 도서 (논리 삭제된 리뷰까지 그 도서의 리뷰, 좋아요, 댓글을 모두 지운다)
   리턴값 : 삭제 완료된 리뷰 수. 리뷰가 없으면 0
  */
  long deleteReviewsWithRelatedDataByBook(Book book);
}
