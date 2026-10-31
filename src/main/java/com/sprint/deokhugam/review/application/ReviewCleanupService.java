package com.sprint.deokhugam.review.application;

import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.comment.application.provided.command.CommentCommand;
import com.sprint.deokhugam.review.application.required.ReviewLikeRepositoryPort;
import com.sprint.deokhugam.review.application.required.ReviewRepositoryPort;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ReviewCleanupService implements ReviewCleanupUseCase {
  private final ReviewRepositoryPort reviewRepositoryPort;
  private final ReviewLikeRepositoryPort reviewLikeRepositoryPort;
  private final CommentCommand commentCommand;

  @Override
  @Transactional
  public long deleteReviewsWithRelatedDataByBook(Book book) {
    List<UUID> reviewIds = reviewRepositoryPort.getIdsByBookId(book.getId());
    if (reviewIds.isEmpty()) return 0L;

    // FK 자식부터 지운다: 좋아요, 댓글, 그다음 리뷰
    reviewLikeRepositoryPort.deleteAllByReviewIds(reviewIds);
    commentCommand.deleteAllByReviewIds(reviewIds);
    return reviewRepositoryPort.deleteAllByIds(reviewIds);
  }
}
