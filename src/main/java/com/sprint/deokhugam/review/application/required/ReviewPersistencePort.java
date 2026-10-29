package com.sprint.deokhugam.review.application.required;

import com.sprint.deokhugam.review.application.dto.req.ReviewSearchCondition;
import com.sprint.deokhugam.review.domain.Review;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewPersistencePort {
  Review save(Review review);

  Review getByIdOrThrow(UUID reviewId); // 일반 기능 + 다른 도메인용 조회 쿼리

  Optional<Review> getByIdIncludingDeleted(UUID reviewId);

  List<Review> getByIdsIncludingDeleted(List<UUID> reviewIds);

  void requireActiveById(UUID reviewId);

  boolean existsByUserIdAndBookId(UUID userId, UUID bookId);

  void delete(Review review);

  long deleteAllByIds(List<UUID> reviewIds);

  void decreaseLikeCountsByUserIds(List<UUID> userIds);

  List<Review> search(ReviewSearchCondition condition);

  long count(ReviewSearchCondition condition);

  List<UUID> getIdsByUserIds(List<UUID> userIds);
}
