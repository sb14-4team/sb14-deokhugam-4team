package com.sprint.deokhugam.review.application;

import com.sprint.deokhugam.book.application.provided.query.BookFinder;
import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.review.application.dto.ReviewResponse;
import com.sprint.deokhugam.review.application.dto.req.CreateReviewCommand;
import com.sprint.deokhugam.review.application.provided.command.ReviewCommand;
import com.sprint.deokhugam.review.domain.Review;
import com.sprint.deokhugam.user.application.provided.query.UserFinder;
import com.sprint.deokhugam.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 객체를 받아와서 앱서비스에서 넣어주는 레지스터 인터페이스 ? -> 무시해도됨
@Service
@RequiredArgsConstructor
public class ReviewApplicationService {
  private final ReviewCommand reviewCommand;

  private final BookFinder bookFinder;
  private final UserFinder userFinder;

  @Transactional
  public ReviewResponse createReview(CreateReviewCommand command) {
    User user = userFinder.findById(command.userId());
    Book book = bookFinder.findById(command.bookId());

    Review review = Review.init(command.rating(), command.content(), user, book);
    Review savedReview = reviewCommand.create(review);

    return ReviewResponse.from(
        savedReview, false, savedReview.getCreatedAt(), savedReview.getUpdatedAt());
  }
}
