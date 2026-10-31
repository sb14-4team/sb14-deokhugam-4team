package com.sprint.deokhugam.review.application;

import com.sprint.deokhugam.book.domain.Book;

public interface ReviewCleanupUseCase {
  /*
   도서 물리 삭제시에 호출하는 메서드
   요청인자 : 삭제할 도서 (논리 삭제된 리뷰까지 그 도서의 리뷰, 좋아요, 댓글을 모두 지운다)
   리턴값 : 삭제 완료된 리뷰 수. 리뷰가 없으면 0
  */
  long deleteReviewsWithRelatedDataByBook(Book book);
}
