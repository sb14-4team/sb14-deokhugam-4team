package com.sprint.deokhugam.review.application;

import com.sprint.deokhugam.book.application.provided.query.BookFinder;
import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.review.application.dto.req.CreateReviewCommand;
import com.sprint.deokhugam.review.application.dto.res.ReviewResponse;
import com.sprint.deokhugam.review.application.provided.command.ReviewCommand;
import com.sprint.deokhugam.review.application.provided.query.ReviewFinder;
import com.sprint.deokhugam.review.application.required.ReviewLikePersistencePort;
import com.sprint.deokhugam.review.domain.Review;
import com.sprint.deokhugam.user.application.provided.query.UserFinder;
import com.sprint.deokhugam.user.domain.User;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 객체를 받아와서 앱서비스에서 넣어주는 레지스터 인터페이스 ? -> 무시해도됨
@Service
@RequiredArgsConstructor
public class ReviewApplicationService {
  private final ReviewCommand reviewCommand;
  private final ReviewFinder reviewFinder;

  private final ReviewLikePersistencePort reviewLikePersistencePort; // 굳이 쿼리서비스를 두는건 별로같음

  private final BookFinder bookFinder;
  private final UserFinder userFinder;

  @Transactional
  public ReviewResponse createReview(CreateReviewCommand command) {
    User user = userFinder.findById(command.userId());
    Book book = bookFinder.findById(command.bookId());

    Review review = Review.init(command.rating(), command.content(), user, book);
    Review savedReview = reviewCommand.createReview(review);

    return ReviewResponse.of(savedReview, false);
  }

  @Transactional(readOnly = true)
  public ReviewResponse getReviewDetail(UUID reviewId, UUID userId) {
    Review review = reviewFinder.getByIdOrThrow(reviewId);
    boolean likedByMe = reviewLikePersistencePort.isLikedBy(reviewId, userId);

    return ReviewResponse.of(review, likedByMe);
  }

  @Transactional
  public ReviewResponse updateReview(UUID userId, UUID reviewId, String newContent, int newRating) {
    userFinder.findById(userId);

    Review review = reviewCommand.updateReview(reviewId, userId, newContent, newRating);
    boolean likedByMe = reviewLikePersistencePort.isLikedBy(reviewId, userId);

    return ReviewResponse.of(review, likedByMe);
  }
}
