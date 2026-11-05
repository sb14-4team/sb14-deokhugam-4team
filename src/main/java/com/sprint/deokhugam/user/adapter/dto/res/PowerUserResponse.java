package com.sprint.deokhugam.user.adapter.dto.res;

import com.sprint.deokhugam.user.application.dto.result.PowerUserResult;
import com.sprint.deokhugam.user.domain.ranking.RankingPeriod;
import java.time.Instant;
import java.util.UUID;

public record PowerUserResponse(
    UUID userId,
    String nickname,
    RankingPeriod period,
    Instant createdAt,
    long rank,
    double score,
    double reviewScoreSum,
    long likeCount,
    long commentCount) {

  public static PowerUserResponse from(PowerUserResult result) {
    return new PowerUserResponse(
        result.userId(),
        result.nickname(),
        result.period(),
        result.createdAt(),
        result.rank(),
        result.score(),
        result.reviewScoreSum(),
        result.likeCount(),
        result.commentCount());
  }
}
