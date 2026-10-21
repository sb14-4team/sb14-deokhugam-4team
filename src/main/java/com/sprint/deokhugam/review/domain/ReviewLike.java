package com.sprint.deokhugam.review.domain;

import com.sprint.deokhugam.common.entity.BaseEntity;
import com.sprint.deokhugam.user.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@Entity
@Table(name = "review_likes")
public class ReviewLike extends BaseEntity {

  @ManyToOne
  @JoinColumn(name = "review_id")
  Review review;

  @ManyToOne
  @JoinColumn(name = "user_id")
  User user;

  boolean likedByMe;
}
