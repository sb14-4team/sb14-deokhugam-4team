package com.sprint.deokhugam.review.application.provided.command;

import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.comment.application.CommentCommandService;
import com.sprint.deokhugam.review.application.required.ReviewLikePersistencePort;
import com.sprint.deokhugam.review.application.required.ReviewPersistencePort;
import com.sprint.deokhugam.review.domain.Review;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 유저   = 유저 앱 서비스 -> 유저 + 유저스테이터스 패키지의 커맨드 유스케이스 호출
 프라이빗 파이널로 유저 삭제 유스케이스
*/
@RequiredArgsConstructor
@Service
public class ReviewCommandService implements ReviewCommand {
  private final ReviewPersistencePort reviewPersistencePort;
  private final ReviewLikePersistencePort reviewLikePersistencePort;
  private final CommentCommandService commentCommandService;

  @Override
  @Transactional
  public long deleteReviewByIds(List<UUID> reviewIds) {
    if (reviewIds.isEmpty()) return 0L;
    List<Review> reviews = reviewPersistencePort.getByIdsIncludingDeleted(reviewIds);

    /*
      10.28 일단 집계 반영안하고 나중에 생각해보기로 결정
       UUID bookId,
       int removedReviewCount,
       long removedRatingSum
       형태로 리스트<dto> 로 전달
    */

    return reviewPersistencePort.deleteAllByIds(reviewIds);
  }

  @Override
  @Transactional
  public long deleteReviewsWithRelatedDataByBook(Book book) {
    List<UUID> reviewIds = reviewPersistencePort.getIdsByBookId(book.getId());
    if (reviewIds.isEmpty()) return 0L;

    // FK 자식부터 지운다: 좋아요, 댓글, 그다음 리뷰
    reviewLikePersistencePort.deleteAllByReviewIds(reviewIds);
    commentCommandService.deleteAllByReviewIds(reviewIds);
    return reviewPersistencePort.deleteAllByIds(reviewIds);
  }
}
