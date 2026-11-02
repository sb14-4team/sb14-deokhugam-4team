package com.sprint.deokhugam.user.domain.ranking;

import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.common.exception.CommonErrorCode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public sealed interface Period permits Period.AllTime, Period.Daily, Period.Monthly, Period.Weekly {

  UserActivityMetrics metrics();

  default long scoreUnits() {
    return metrics().scoreUnits();
  }

  default Period withMetrics(UserActivityMetrics changeMetrics) {
    if (changeMetrics == null) {
      throw new BusinessException(CommonErrorCode.INVALID_INPUT);
    }

    return switch (this) {
      case Daily daily -> new Daily(daily.date, changeMetrics);
      case Weekly weekly -> new Weekly(weekly.week, changeMetrics);
      case Monthly monthly -> new Monthly(monthly.month, changeMetrics);
      case AllTime allTime -> new AllTime(changeMetrics);
    };
  }

  boolean covers(LocalDate date);

  record Daily(LocalDate date, UserActivityMetrics metrics) implements Period {

    public Daily {
      if (date == null || metrics == null) {
        throw new BusinessException(CommonErrorCode.INVALID_INPUT);
      }
    }

    @Override
    public boolean covers(LocalDate date) {
      return date.isEqual(date());
    }
  }

  record Weekly(LocalDate week, UserActivityMetrics metrics) implements Period {

    public Weekly {
      if (week == null || metrics == null) {
        throw new BusinessException(CommonErrorCode.INVALID_INPUT);
      }

      week = week.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    @Override
    public boolean covers(LocalDate targetDate) {
      return !targetDate.isBefore(week) && targetDate.isBefore(week.plusWeeks(1));
    }
  }

  record Monthly(LocalDate month, UserActivityMetrics metrics) implements Period {

    public Monthly {

      if (month == null || metrics == null) {
        throw new BusinessException(CommonErrorCode.INVALID_INPUT);
      }

      month = month.withDayOfMonth(1);
    }

    @Override
    public boolean covers(LocalDate targetDate) {
      return !targetDate.isBefore(month) && targetDate.isBefore(month.plusMonths(1));
    }
  }

  record AllTime(UserActivityMetrics metrics) implements Period {

    public AllTime {
      if (metrics == null) {
        throw new BusinessException(CommonErrorCode.INVALID_INPUT);
      }
    }

    @Override
    public boolean covers(LocalDate date) {
      return true;
    }
  }

  static Period daily(LocalDate date, UserActivityMetrics metrics) {
    return new Daily(date, metrics);
  }

  static Period weekly(LocalDate date, UserActivityMetrics metrics) {
    return new Weekly(date, metrics);
  }

  static Period monthly(LocalDate date, UserActivityMetrics metrics) {
    return new Monthly(date, metrics);
  }

  static Period allTime(UserActivityMetrics metrics) {
    return new AllTime(metrics);
  }
}
