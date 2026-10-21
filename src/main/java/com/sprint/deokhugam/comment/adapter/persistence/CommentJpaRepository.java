package com.sprint.deokhugam.comment.adapter.persistence;

import com.sprint.deokhugam.comment.domain.Comment;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentJpaRepository extends JpaRepository<Comment, UUID> {}
