package com.sprint.deokhugam.user.domain.ranking;

import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.common.exception.CommonErrorCode;
import com.sprint.deokhugam.user.domain.ranking.Period.AllTime;
import com.sprint.deokhugam.user.domain.ranking.Period.Daily;
import com.sprint.deokhugam.user.domain.ranking.Period.Monthly;
import com.sprint.deokhugam.user.domain.ranking.Period.Weekly;
import java.time.LocalDate;

public record PeriodStats(
    Period.Daily daily, Period.Weekly weekly, Period.Monthly monthly, Period.AllTime allTime) {

  public PeriodStats {
    if (daily == null || weekly == null || monthly == null || allTime == null) {
      throw new BusinessException(CommonErrorCode.INVALID_INPUT);
    }
  }

  public static PeriodStats empty(LocalDate date) {
    if (date == null) {
      throw new BusinessException(CommonErrorCode.INVALID_INPUT);
    }
    return new PeriodStats(
        new Daily(date, UserActivityMetrics.EMPTY),
        new Weekly(date, UserActivityMetrics.EMPTY),
        new Monthly(date, UserActivityMetrics.EMPTY),
        new AllTime(UserActivityMetrics.EMPTY));
  }

  public PeriodStats withPeriod(Period updated) {
    if (updated == null) {
      throw new BusinessException(CommonErrorCode.INVALID_INPUT);
    }
    return switch (updated) {
      case Daily updatedDaily -> new PeriodStats(updatedDaily, weekly, monthly, allTime);
      case Weekly updatedWeekly -> new PeriodStats(daily, updatedWeekly, monthly, allTime);
      case Monthly updatedMonthly -> new PeriodStats(daily, weekly, updatedMonthly, allTime);
      case AllTime updatedAlltime -> new PeriodStats(daily, weekly, monthly, updatedAlltime);
    };
  }
}
