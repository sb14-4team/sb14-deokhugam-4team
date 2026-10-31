package com.sprint.deokhugam.review.application.required;

import com.sprint.deokhugam.review.domain.Review;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepositoryPort {
  Review save(Review review);

  Review getByIdOrThrow(UUID reviewId);

  Optional<Review> getByIdIncludingDeleted(UUID reviewId);

  List<Review> getByIdsIncludingDeleted(List<UUID> reviewIds);

  void requireActiveById(UUID reviewId);

  boolean existsByUserIdAndBookId(UUID userId, UUID bookId);

  void delete(Review review);

  long deleteAllByIds(List<UUID> reviewIds);

  void decreaseLikeCountsByUserId(UUID userId);

  List<UUID> getIdsByUserIds(List<UUID> userIds);

  // 논리 삭제된 리뷰도 포함 (도서 물리 삭제용)
  List<UUID> getIdsByBookId(UUID bookId);

  List<UUID> getIdsByUserIdIncludingDeleted(UUID userId);
}
