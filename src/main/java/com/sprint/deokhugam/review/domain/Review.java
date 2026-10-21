package com.sprint.deokhugam.review.domain;

import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.common.entity.BaseSoftDeletableEntity;
import com.sprint.deokhugam.user.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
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

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  UUID id;

  Integer rating;
  String content;
  Integer likeCount = 0;
  Integer commentCount = 0;

  @OneToMany List<ReviewLike> reviewLike;

  @ManyToOne
  @JoinColumn(name = "user_id")
  User user;

  @ManyToOne
  @JoinColumn(name = "book_id")
  Book book;

  public static Review init(Integer rate, String content) {
    return builder().rating(rate).content(content).build();
  }

  public void countLike() {
    likeCount++;
  }
}
