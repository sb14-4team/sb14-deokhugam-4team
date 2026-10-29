package com.sprint.deokhugam.review.application.provided.command;

import com.sprint.deokhugam.book.application.provided.command.BookReviewStatsCommand;
import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.comment.application.CommentCommandService;
import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.domain.review.exception.ReviewErrorCode;
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
  private final BookReviewStatsCommand bookReviewStatsCommand;

  @Override
  @Transactional
  public Review createReview(Review review) {
    if (reviewPersistencePort.existsByUserIdAndBookId(
        review.getUser().getId(), review.getBook().getId())) {
      throw new BusinessException(ReviewErrorCode.ALREADY_EXISTS);
    }

    Review saved = reviewPersistencePort.save(review);
    bookReviewStatsCommand.addReview(saved.getBook(), saved.getRating());
    return saved;
  }

  @Override
  @Transactional
  public Review updateReview(UUID reviewId, UUID userId, String newContent, int newRating) {
    Review review = reviewPersistencePort.getByIdOrThrow(reviewId);

    if (!review.verifyReviewUpdate(userId)) {
      throw new BusinessException(ReviewErrorCode.FORBIDDEN);
    }

    int oldRating = review.getRating();
    review.updateContentAndRating(newContent, newRating);
    bookReviewStatsCommand.changeReviewRating(review.getBook(), oldRating, newRating);
    return review;
  }

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
  public long deleteReviewsWithRelatedDataByBook(Book book) {

    // commentCommandService

    // reviewLikePersistencePort.deleteAllByReviewIds(reviewIds);
    // return reviewPersistencePort.deleteAllByIds(reviewIds);
    return 0L;
  }
}
