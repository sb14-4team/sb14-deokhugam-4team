package com.sprint.deokhugam.review.domain;

import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.common.entity.BaseSoftDeletableEntity;
import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.domain.review.exception.ReviewErrorCode;
import com.sprint.deokhugam.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "reviews")
public class Review extends BaseSoftDeletableEntity {
  @Column(name = "rating", nullable = false)
  Integer rating;

  @Column(name = "content", nullable = false)
  String content;

  @Builder.Default
  @Column(name = "like_count", nullable = false)
  Integer likeCount = 0;

  @Builder.Default
  @Column(name = "comment_count", nullable = false)
  Integer commentCount = 0;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "book_id")
  Book book;

  public static Review init(Integer rate, String content, User user, Book book) {
    return builder().rating(rate).content(content).user(user).book(book).build();
  }

  public void updateContentAndRating(String content, int rating) {
    if (rating < 1 || rating > 5) {
      throw new BusinessException(ReviewErrorCode.INVALID_RATING);
    }
    this.content = content;
    this.rating = rating;
  }

  public boolean verifyReviewUpdate(UUID userId) {
    return this.getUser().getId().equals(userId);
  }

  public void increaseLikeCount() {
    likeCount++;
  }
}
