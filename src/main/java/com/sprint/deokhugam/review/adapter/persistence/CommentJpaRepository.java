package com.sprint.deokhugam.review.adapter.persistence;

import com.sprint.deokhugam.review.domain.Comment;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentJpaRepository extends JpaRepository<Comment, UUID> {}
