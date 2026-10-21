package com.sprint.deokhugam.review.domain;

import com.sprint.deokhugam.common.entity.BaseSoftDeletableEntity;
import com.sprint.deokhugam.user.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class Comment extends BaseSoftDeletableEntity {

  @ManyToOne
  @JoinColumn(name = "review_id")
  Review review;

  @ManyToOne
  @JoinColumn(name = "user_id")
  User user;

  String content;
}
