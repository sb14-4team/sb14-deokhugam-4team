package com.sprint.deokhugam.user.application;

import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.common.pagination.CursorPageResponse;
import com.sprint.deokhugam.user.application.dto.PowerUserCursor;
import com.sprint.deokhugam.user.application.dto.PowerUserDto;
import com.sprint.deokhugam.user.application.dto.PowerUserSearchCond;
import com.sprint.deokhugam.user.application.dto.UserDto;
import com.sprint.deokhugam.user.application.dto.UserLoginRequest;
import com.sprint.deokhugam.user.application.provided.query.UserFinder;
import com.sprint.deokhugam.user.application.provided.query.UserLogin;
import com.sprint.deokhugam.user.application.required.PasswordHasher;
import com.sprint.deokhugam.user.application.required.UserRepository;
import com.sprint.deokhugam.user.domain.Email;
import com.sprint.deokhugam.user.domain.User;
import com.sprint.deokhugam.user.domain.exception.UserErrorCode;
import com.sprint.deokhugam.user.domain.ranking.Period;
import com.sprint.deokhugam.user.domain.ranking.PeriodStats;
import com.sprint.deokhugam.user.domain.ranking.RankingPeriod;
import com.sprint.deokhugam.user.domain.ranking.UserActivityMetrics;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserQueryService implements UserFinder, UserLogin {

  private final UserRepository userRepository;
  private final PasswordHasher passwordHasher;

  @Override
  public User findById(UUID userId) {
    return userRepository
        .findById(userId)
        .filter(user -> !user.isDeleted())
        .orElseThrow(() -> new BusinessException(UserErrorCode.NOT_FOUND));
  }

  @Override
  public UserDto getById(UUID userId) {
    User user = findById(userId);
    return UserDto.to(
        user.getId(), user.getEmail().value(), user.getNickname(), user.getCreatedAt());
  }

  @Override
  public UserDto login(UserLoginRequest request) {
    Email email = Email.to(request.email());
    User user = findByEmail(email);
    boolean matches = passwordHasher.matches(request.password(), user.getPassword().password());
    if (!matches) {
      throw new BusinessException(UserErrorCode.LOGIN_FAILED);
    }
    return UserDto.to(
        user.getId(), user.getEmail().value(), user.getNickname(), user.getCreatedAt());
  }

  @Override
  public List<UUID> findCleanupIds(Instant cutoff, int limit) {
    return userRepository.findCleanupIds(cutoff, limit);
  }

  @Override
  public List<UUID> findRankingTargetIds() {
    return userRepository.findRankingTargetIds();
  }

  @Override
  @Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
  public CursorPageResponse<PowerUserDto> findPowerUsers(PowerUserSearchCond cond) {
    LocalDate publishedOn = LocalDate.now(ZoneId.of("Asia/Seoul"));
    List<User> rows = userRepository.findPowerUsers(cond, publishedOn);
    long totalElements = userRepository.countPowerUsers(publishedOn);
    CursorPageResponse<User> pageResponse =
        CursorPageResponse.of(
            rows,
            cond.limit(),
            user -> cursorOf(user, cond.period(), publishedOn).encode(),
            User::getCreatedAt,
            totalElements);
    if (pageResponse.content().isEmpty()) {
      return new CursorPageResponse<>(List.of(), null, null, 0, totalElements, false);
    }

    User first = pageResponse.content().getFirst();
    long firstRank =
        userRepository.countPowerUsersAhead(
                cursorOf(first, cond.period(), publishedOn), first.getCreatedAt())
            + 1;

    List<PowerUserDto> content = new ArrayList<>(pageResponse.size());

    for (int i = 0; i < pageResponse.size(); i++) {
      long rank = cond.direction() == Direction.ASC ? firstRank + i : firstRank - i;
      content.add(toPowerUserDto(pageResponse.content().get(i), cond.period(), rank));
    }
    return new CursorPageResponse<>(
        content,
        pageResponse.nextCursor(),
        pageResponse.nextAfter(),
        pageResponse.size(),
        pageResponse.totalElements(),
        pageResponse.hasNext());
  }

  private PowerUserCursor cursorOf(User user, RankingPeriod period, LocalDate publishedOn) {
    return new PowerUserCursor(period, publishedOn, scoreUnits(user, period), user.getId());
  }

  private PowerUserDto toPowerUserDto(User user, RankingPeriod period, long rank) {
    PeriodStats published = user.getRankingStats().published();

    Period selected =
        switch (period) {
          case DAILY -> published.daily();
          case WEEKLY -> published.weekly();
          case MONTHLY -> published.monthly();
          case ALL_TIME -> published.allTime();
        };

    UserActivityMetrics metrics = selected.metrics();

    double reviewScoreSum =
        (metrics.reviewLikeCount() * 3L + metrics.reviewCommentCount() * 7L) / 10.0;

    return new PowerUserDto(
        user.getId(),
        user.getNickname(),
        period,
        user.getCreatedAt(),
        rank,
        scoreUnits(user, period) / 100.0,
        reviewScoreSum,
        metrics.participatedLikeCount(),
        metrics.participatedCommentCount());
  }

  private long scoreUnits(User user, RankingPeriod period) {
    return switch (period) {
      case DAILY -> user.getDailyScore();
      case WEEKLY -> user.getWeeklyScore();
      case MONTHLY -> user.getMonthlyScore();
      case ALL_TIME -> user.getAllTimeScore();
    };
  }

  private User findByEmail(Email email) {
    return userRepository
        .findByEmail(email)
        .filter(user -> !user.isDeleted())
        .orElseThrow(() -> new BusinessException(UserErrorCode.LOGIN_FAILED));
  }
}
