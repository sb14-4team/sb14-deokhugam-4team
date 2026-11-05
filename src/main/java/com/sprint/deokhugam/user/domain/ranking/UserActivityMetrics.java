package com.sprint.deokhugam.user.domain.ranking;

import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.common.exception.CommonErrorCode;

public record UserActivityMetrics(
    long reviewLikeCount,
    long reviewCommentCount,
    long participatedLikeCount,
    long participatedCommentCount) {

  public static final UserActivityMetrics EMPTY = new UserActivityMetrics(0, 0, 0, 0);

  public UserActivityMetrics {
    if (reviewLikeCount < 0
        || reviewCommentCount < 0
        || participatedLikeCount < 0
        || participatedCommentCount < 0) {
      throw new BusinessException(CommonErrorCode.INVALID_INPUT);
    }
  }

  public long scoreUnits() {
    return reviewLikeCount * 15L
        + reviewCommentCount * 35L
        + participatedLikeCount * 20L
        + participatedCommentCount * 30L;
  }

  public UserActivityMetrics changeReviewLikeCount(long count) {
    return new UserActivityMetrics(
        reviewLikeCount + count,
        reviewCommentCount,
        participatedLikeCount,
        participatedCommentCount);
  }

  public UserActivityMetrics changeReviewCommentCount(long count) {
    return new UserActivityMetrics(
        reviewLikeCount,
        reviewCommentCount + count,
        participatedLikeCount,
        participatedCommentCount);
  }

  public UserActivityMetrics changeParticipatedLikeCount(long count) {
    return new UserActivityMetrics(
        reviewLikeCount,
        reviewCommentCount,
        participatedLikeCount + count,
        participatedCommentCount);
  }

  public UserActivityMetrics changeParticipatedCommentCount(long count) {
    return new UserActivityMetrics(
        reviewLikeCount,
        reviewCommentCount,
        participatedLikeCount,
        participatedCommentCount + count);
  }
}
