package com.sprint.deokhugam.comment.domain;

import com.sprint.deokhugam.common.entity.BaseSoftDeletableEntity;
import com.sprint.deokhugam.review.domain.Review;
import com.sprint.deokhugam.user.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
@Table(name = "comments")
public class Comment extends BaseSoftDeletableEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "review_id")
  Review review;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  User user;

  String content;

  public static Comment create(Review review, User user, String content) {
    return new Comment(review, user, content);
  }

  public void update(String newContent) {
    this.content = newContent;
  }
}
