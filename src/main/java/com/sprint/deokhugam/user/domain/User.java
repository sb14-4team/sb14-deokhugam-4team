package com.sprint.deokhugam.user.domain;

import com.sprint.deokhugam.common.entity.BaseSoftDeletableEntity;
import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.common.exception.CommonErrorCode;
import com.sprint.deokhugam.user.domain.exception.UserErrorCode;
import com.sprint.deokhugam.user.domain.ranking.PeriodStats;
import com.sprint.deokhugam.user.domain.ranking.UserRankingStats;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseSoftDeletableEntity {

  @Column(name = "name", nullable = false, length = 20)
  private String nickname;

  @Column(name = "email", nullable = false, unique = true)
  private Email email;

  @Column(name = "password", nullable = false)
  private Password password;

  @Column(name = "ranking_stats", columnDefinition = "text")
  private UserRankingStats rankingStats;

  @ColumnDefault("0")
  @Column(name = "daily_score", nullable = false)
  private long dailyScore;

  @ColumnDefault("0")
  @Column(name = "weekly_score", nullable = false)
  private long weeklyScore;

  @ColumnDefault("0")
  @Column(name = "monthly_score", nullable = false)
  private long monthlyScore;

  @ColumnDefault("0")
  @Column(name = "all_time_score", nullable = false)
  private long allTimeScore;

  @Column(name = "ranking_published_on")
  private LocalDate rankingPublishedOn;

  private User(String nickname, Email email, Password password) {
    this.nickname = nickname;
    this.email = email;
    this.password = password;
  }

  public void updateNickname(String nickname) {
    if (this.nickname.equals(nickname)) {
      throw new BusinessException(UserErrorCode.DUPLICATE_NICKNAME);
    }
    this.nickname = nickname;
  }

  public void updateRankingStats(UserRankingStats updatedStats) {
    if (updatedStats == null) {
      throw new BusinessException(CommonErrorCode.INVALID_INPUT);
    }
    this.rankingStats = updatedStats;
    this.rankingPublishedOn = updatedStats.lastPublished();

    PeriodStats published = updatedStats.published();

    this.dailyScore = published.daily().scoreUnits();
    this.weeklyScore = published.weekly().scoreUnits();
    this.monthlyScore = published.monthly().scoreUnits();
    this.allTimeScore = published.allTime().scoreUnits();
  }

  public void initializeRankingStats(LocalDate date) {
    if (this.rankingStats != null) {
      throw new BusinessException(CommonErrorCode.INVALID_INPUT);
    }

    updateRankingStats(UserRankingStats.empty(date));
  }

  public static User create(String nickname, Email email, Password password) {
    return new User(nickname, email, password);
  }
}
