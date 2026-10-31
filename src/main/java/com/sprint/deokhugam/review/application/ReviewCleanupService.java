package com.sprint.deokhugam.review.application;

import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.comment.application.provided.command.CommentCommand;
import com.sprint.deokhugam.review.application.required.ReviewLikeRepositoryPort;
import com.sprint.deokhugam.review.application.required.ReviewRepositoryPort;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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

  /*
   1. 리뷰리스트 조회를 통해 삭제할 리뷰가 없다면 나머지 쿼리를 진행하지 않고 바로 리턴가능
   2. 딜리트 서브쿼리로 최초 삭제 리뷰리스트 조회 없이 쿼리 최적화 가능 - 삭제할 리뷰가 없어도 쿼리가 날라감
   1번으로 진행

   - 현재 도서 집계는 반영 X
   UUID bookId,
   int removedReviewCount,
   long removedRatingSum
  */
  @Override
  @Transactional
  public long deleteReviewsWithRelatedDataByUserId(UUID userId) {
    String stage = "리뷰 ID 조회";
    List<UUID> reviewIds = List.of();

    try {
      reviewIds = reviewRepositoryPort.getIdsByUserIdIncludingDeleted(userId);
      if (reviewIds.isEmpty()) return 0L;

      stage = "댓글 삭제";
      commentCommand.deleteAllByReviewIds(reviewIds);

      stage = "좋아요 삭제";
      reviewLikeRepositoryPort.deleteAllByReviewIds(reviewIds);

      stage = "리뷰 삭제";
      return reviewRepositoryPort.deleteAllByIds(reviewIds);
    } catch (RuntimeException e) {
      log.error(
          "탈퇴 사용자 리뷰 최종 정리 실패 -- userID = {}, 실패영역 = {}, 삭제 리뷰 수 = {}",
          userId,
          stage,
          reviewIds.size(),
          e);
      throw e;
    }
  }
}
