package com.sprint.deokhugam.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

// @Scheduled가 붙은 메서드를 정해진 시각에 실행하게 켠다.
// test 프로필에서는 꺼진다. 테스트 중에 작업이 저절로 돌지 않게 하려고.
//
// 사용 예 (도메인에서):
//   @Scheduled(cron = "0 5 0 * * *", zone = "Asia/Seoul") // 매일 00:05
//   public void run() { dashboardService.aggregate(LocalDate.now(SEOUL).minusDays(1)); }
// - 스케줄러 메서드는 서비스 메서드를 부르기만 한다. 테스트는 서비스 메서드를 직접 부른다.
// - 실행 스레드가 1개라 작업은 하나씩 차례로 돈다. 로그의 [scheduler-1]로 구분한다.
@Configuration
@EnableScheduling
@Profile("!test")
public class SchedulingConfig {}
