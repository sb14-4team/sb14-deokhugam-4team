package com.sprint.deokhugam.user.domain.ranking;

import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.common.exception.CommonErrorCode;
import com.sprint.deokhugam.user.domain.ranking.Period.AllTime;
import com.sprint.deokhugam.user.domain.ranking.Period.Daily;
import com.sprint.deokhugam.user.domain.ranking.Period.Monthly;
import com.sprint.deokhugam.user.domain.ranking.Period.Weekly;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public record UserRankingStats(
    PeriodStats current, PeriodStats published, LocalDate lastPublished) {

  public UserRankingStats {
    if (current == null || published == null) {
      throw new BusinessException(CommonErrorCode.INVALID_INPUT);
    }
  }

  public static UserRankingStats empty(LocalDate date) {
    if (date == null) {
      throw new BusinessException(CommonErrorCode.INVALID_INPUT);
    }
    LocalDate previousWeek =
        date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusWeeks(1);
    LocalDate previousMonth = date.withDayOfMonth(1).minusMonths(1);

    PeriodStats published =
        new PeriodStats(
            new Daily(date.minusDays(1), UserActivityMetrics.EMPTY),
            new Weekly(previousWeek, UserActivityMetrics.EMPTY),
            new Monthly(previousMonth, UserActivityMetrics.EMPTY),
            new AllTime(UserActivityMetrics.EMPTY));

    return new UserRankingStats(PeriodStats.empty(date), published, null);
  }

  public UserRankingStats closeDaily(LocalDate today) {
    if (today == null) {
      throw new BusinessException(CommonErrorCode.INVALID_INPUT);
    }

    Daily currentDaily = current.daily();

    if (currentDaily.date().equals(today)) {
      return this;
    }

    LocalDate yesterday = today.minusDays(1);

    if (!currentDaily.date().equals(yesterday)) {
      throw new BusinessException(CommonErrorCode.INVALID_INPUT);
    }
    PeriodStats nextPublished = published.withPeriod(currentDaily);

    PeriodStats nextCurrent = current.withPeriod(new Daily(today, UserActivityMetrics.EMPTY));
    return new UserRankingStats(nextCurrent, nextPublished, lastPublished);
  }

  public UserRankingStats closeWeekly(LocalDate week) {
    if (week == null || !week.getDayOfWeek().equals(DayOfWeek.MONDAY)) {
      throw new BusinessException(CommonErrorCode.INVALID_INPUT);
    }

    Weekly currentWeekly = current.weekly();

    if (currentWeekly.week().equals(week)) {
      return this;
    }

    LocalDate lastWeek = week.minusWeeks(1);

    if (!currentWeekly.week().equals(lastWeek)) {
      throw new BusinessException(CommonErrorCode.INVALID_INPUT);
    }

    PeriodStats nextPublished = published.withPeriod(currentWeekly);

    PeriodStats nextCurrent = current.withPeriod(new Weekly(week, UserActivityMetrics.EMPTY));
    return new UserRankingStats(nextCurrent, nextPublished, lastPublished);
  }

  public UserRankingStats closeMonthly(LocalDate month) {
    if (month == null || month.getDayOfMonth() != 1) {
      throw new BusinessException(CommonErrorCode.INVALID_INPUT);
    }
    LocalDate thisMonth = month.withDayOfMonth(1);

    Monthly currentMonth = current.monthly();
    if (currentMonth.month().equals(thisMonth)) {
      return this;
    }
    LocalDate lastMonth = month.minusMonths(1);

    if (!currentMonth.month().equals(lastMonth)) {
      throw new BusinessException(CommonErrorCode.INVALID_INPUT);
    }

    PeriodStats nextPublished = published.withPeriod(currentMonth);

    PeriodStats nextCurrent = current.withPeriod(new Monthly(month, UserActivityMetrics.EMPTY));

    return new UserRankingStats(nextCurrent, nextPublished, lastPublished);
  }

  public UserRankingStats close(LocalDate today) {
    if (today == null) {
      throw new BusinessException(CommonErrorCode.INVALID_INPUT);
    }

    if (lastPublished != null) {
      if (today.isBefore(lastPublished)) {
        throw new BusinessException(CommonErrorCode.INVALID_INPUT);
      }

      if (today.equals(lastPublished)) {
        return this;
      }
    }
    UserRankingStats result = closeDaily(today);

    if (today.getDayOfWeek() == DayOfWeek.MONDAY) {
      result = result.closeWeekly(today);
    }
    if (today.getDayOfMonth() == 1) {
      result = result.closeMonthly(today);
    }

    PeriodStats nextPublished = result.published().withPeriod(result.current().allTime());

    return new UserRankingStats(result.current(), nextPublished, today);
  }
}
