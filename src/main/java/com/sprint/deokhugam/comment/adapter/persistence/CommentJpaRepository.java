package com.sprint.deokhugam.comment.adapter.persistence;

import com.sprint.deokhugam.comment.domain.Comment;
import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.domain.comment.exception.CommentErrorCode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentJpaRepository extends JpaRepository<Comment, UUID> {

  Optional<Comment> findByIdAndDeletedAtIsNull(UUID commentId);

  default Comment getByIdOrThrow(UUID commentId) {
    return findByIdAndDeletedAtIsNull(commentId)
        .orElseThrow(() -> new BusinessException(CommentErrorCode.NOT_FOUND));
  }

  List<Comment> findByUser_Id(UUID userId);

  List<Comment> findByReview_Id(UUID reviewId);
}
