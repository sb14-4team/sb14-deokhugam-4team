package com.sprint.deokhugam.review.application;

import com.sprint.deokhugam.book.application.BookCommandService;
import com.sprint.deokhugam.book.application.provided.query.BookFinder;
import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.common.pagination.CursorPageResponse;
import com.sprint.deokhugam.domain.review.exception.ReviewErrorCode;
import com.sprint.deokhugam.review.application.dto.req.CreateReviewCommand;
import com.sprint.deokhugam.review.application.dto.req.ReviewSearchCondition;
import com.sprint.deokhugam.review.application.dto.res.ReviewLikeResponse;
import com.sprint.deokhugam.review.application.dto.res.ReviewResponse;
import com.sprint.deokhugam.review.application.provided.command.ReviewCommand;
import com.sprint.deokhugam.review.application.provided.query.ReviewFinder;
import com.sprint.deokhugam.review.application.required.ReviewLikePersistencePort;
import com.sprint.deokhugam.review.application.required.ReviewPersistencePort;
import com.sprint.deokhugam.review.domain.Review;
import com.sprint.deokhugam.review.domain.ReviewLike;
import com.sprint.deokhugam.user.application.provided.query.UserFinder;
import com.sprint.deokhugam.user.domain.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 객체를 받아와서 앱서비스에서 넣어주는 레지스터 인터페이스 ? -> 무시해도됨
@Service
@RequiredArgsConstructor
public class ReviewApplicationService implements ReviewUseCase {

  private final ReviewCommand reviewCommand;
  private final ReviewFinder reviewFinder;

  private final ReviewLikePersistencePort reviewLikePersistencePort;
  private final ReviewPersistencePort reviewPersistencePort;

  // private final BookReviewStatsCommand bookReviewStatsCommand;
  private final BookFinder bookFinder;
  private final UserFinder userFinder;
  private final BookCommandService bookCommandService;

  @Override
  @Transactional
  public ReviewResponse createReview(CreateReviewCommand command) {
    User user = userFinder.findById(command.userId());
    Book book = bookFinder.findById(command.bookId());

    Review review = Review.create(command.rating(), command.content(), user, book);
    Review savedReview = reviewCommand.createReview(review);

    return ReviewResponse.of(savedReview, false);
  }

  @Override
  @Transactional(readOnly = true)
  public ReviewResponse getReviewDetail(UUID reviewId, UUID requestUserId) {
    Review review = reviewFinder.getByIdOrThrow(reviewId);
    boolean likedByMe = reviewLikePersistencePort.isLikedBy(reviewId, requestUserId);

    return ReviewResponse.of(review, likedByMe);
  }

  @Override
  @Transactional
  public ReviewResponse updateReview(UUID userId, UUID reviewId, String newContent, int newRating) {
    userFinder.findById(userId);

    Review review = reviewCommand.updateReview(reviewId, userId, newContent, newRating);
    boolean likedByMe = reviewLikePersistencePort.isLikedBy(reviewId, userId);

    return ReviewResponse.of(review, likedByMe);
  }

  @Override
  @Transactional(readOnly = true)
  public CursorPageResponse<ReviewResponse> searchReviewsByPagination(
      ReviewSearchCondition condition, UUID requestUserId) {
    List<Review> reviewList = reviewPersistencePort.search(condition);
    long totalElements = reviewPersistencePort.count(condition);

    List<UUID> reviewIds = reviewList.stream().map(Review::getId).toList();

    List<UUID> likeIdsByRequestId =
        reviewIds.isEmpty()
            ? List.of()
            : reviewLikePersistencePort.findReviewLikeIdsByRequestId(reviewIds, requestUserId);

    List<ReviewResponse> responses =
        reviewList.stream()
            .map(review -> ReviewResponse.of(review, likeIdsByRequestId.contains(review.getId())))
            .toList();

    return toCursorPage(responses, condition, totalElements);
  }

  // 자기 리뷰에도 좋아요 표시 가능
  @Override
  @Transactional
  public ReviewLikeResponse toggleLike(UUID reviewId, UUID requestUserId) {
    User user = userFinder.findById(requestUserId);
    Review review = reviewFinder.getByIdOrThrow(reviewId);

    Optional<ReviewLike> foundReviewLike =
        reviewLikePersistencePort.getByUserIdAndReviewId(requestUserId, reviewId);

    ReviewLike reviewLike = toggleOrCreateReviewLike(foundReviewLike, review, user);

    return ReviewLikeResponse.from(reviewLike);
  }

  @Override
  @Transactional
  public void softDeleteReview(UUID reviewId, UUID requestUserId) {
    userFinder.findById(requestUserId);
    Review review = reviewFinder.getByIdOrThrow(reviewId);

    // bookReviewStatsCommand.removeReview(review.getBook().getId(), review.getRating()); - 구현체 x
    // 논리삭제 시 점수반영으로 진행
    if (!review.verifyReviewUpdate(requestUserId)) {
      throw new BusinessException(ReviewErrorCode.FORBIDDEN);
    }

    review.softDelete();
    // 알람 호출한다 - 미구현
  }

  /*
   리뷰 단일 삭제 ( 스웨거 문서 엔드포인트 용도 )
   연관 - 해당 리뷰 좋아요 + 댓글
  */
  @Override
  @Transactional
  public void hardDeleteReview(UUID reviewId, UUID requestUserId) {
    userFinder.findById(requestUserId);
    Review review =
        reviewPersistencePort
            .getByIdIncludingDeleted(reviewId)
            .orElseThrow(() -> new BusinessException(ReviewErrorCode.NOT_FOUND));

    if (!review.getUser().getId().equals(requestUserId))
      throw new BusinessException(ReviewErrorCode.FORBIDDEN);

    reviewLikePersistencePort.deleteAllByReviewId(reviewId);
    // 댓글 삭제 호출 - 아직 미구현

    if (!review.isDeleted()) {
      // bookReviewStatsCommand.removeReview(review.getBook().getId(), review.getRating());
    }

    reviewPersistencePort.delete(review);
  }

  private ReviewLike toggleOrCreateReviewLike(
      Optional<ReviewLike> foundReviewLike, Review review, User user) {
    ReviewLike reviewLike;
    if (foundReviewLike.isPresent()) {
      reviewLike = foundReviewLike.get();
      boolean liked = reviewLike.toggle();

      if (liked) review.increaseLikeCount();
      else review.decreaseLikeCount();
    } else {
      reviewLike = ReviewLike.init(review, user);
      reviewLikePersistencePort.save(reviewLike);
      review.increaseLikeCount();
    }
    return reviewLike;
  }

  private CursorPageResponse<ReviewResponse> toCursorPage(
      List<ReviewResponse> responses, ReviewSearchCondition condition, long totalElements) {
    return CursorPageResponse.of(
        responses,
        condition.limit(),
        response -> "rating".equals(condition.orderBy()) ? response.rating() : response.createdAt(),
        ReviewResponse::createdAt,
        totalElements);
  }
}
