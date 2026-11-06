package com.sprint.deokhugam.dashboard.review.domain;

import com.sprint.deokhugam.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 매일 0시에 기간별로 계산해 인기 리뷰 순위를 저장.
// 리뷰/도서/작성자 정보는 조회할 때 reviewId로 조인
@Entity
@Getter
@Table(
    name = "popular_reviews",
    // 리뷰 데이터 조회 시
    indexes = @Index(name = "idx_popular_reviews_period_rank", columnList = "period, rank"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PopularReview extends BaseEntity {

  private static final double LIKE_WEIGHT = 0.3;
  private static final double COMMENT_WEIGHT = 0.7;

  @Column(name = "review_id", nullable = false)
  private UUID reviewId;

  @Enumerated(EnumType.STRING)
  @Column(name = "period", nullable = false, length = 20)
  private PopularReviewPeriod period;

  @Column(name = "rank", nullable = false)
  private long rank;

  @Column(name = "score", nullable = false)
  private double score;

  @Column(name = "like_count", nullable = false)
  private long likeCount;

  @Column(name = "comment_count", nullable = false)
  private long commentCount;

  private PopularReview(
      UUID reviewId,
      PopularReviewPeriod period,
      long rank,
      double score,
      long likeCount,
      long commentCount) {
    this.reviewId = reviewId;
    this.period = period;
    this.rank = rank;
    this.score = score;
    this.likeCount = likeCount;
    this.commentCount = commentCount;
  }

  public static PopularReview create(
      UUID reviewId, PopularReviewPeriod period, long rank, long likeCount, long commentCount) {
    return new PopularReview(
        reviewId, period, rank, calculateScore(likeCount, commentCount), likeCount, commentCount);
  }

  // 소수 계산 오차를 없애려고 소수 첫째 자리로 맞춘다
  // 가중치가 소수 첫째 자리까지라 실제 점수도 항상 소수 첫째 자리까지다
  public static double calculateScore(long likeCount, long commentCount) {
    double score = (likeCount * LIKE_WEIGHT) + (commentCount * COMMENT_WEIGHT);
    return Math.round(score * 10) / 10.0;
  }
}
