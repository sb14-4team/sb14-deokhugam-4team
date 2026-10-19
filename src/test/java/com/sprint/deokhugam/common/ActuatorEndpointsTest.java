package com.sprint.deokhugam.common;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

// ALB 대상 그룹이 /actuator/health로 태스크 상태를 판단하므로, 이 경로가 깨지면 배포가 롤백된다
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ActuatorEndpointsTest {

  @Autowired private MockMvc mockMvc;

  @Test
  @DisplayName("헬스 체크는 DB 연결까지 포함해 UP을 돌려준다")
  void healthIsUp() throws Exception {
    mockMvc
        .perform(get("/actuator/health"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("UP"));
  }

  @ParameterizedTest
  @ValueSource(strings = {"/actuator/env", "/actuator/beans", "/actuator/configprops"})
  @DisplayName("설정값이 드러나는 엔드포인트는 노출하지 않는다")
  void sensitiveEndpointsAreNotExposed(String path) throws Exception {
    mockMvc.perform(get(path)).andExpect(status().isNotFound());
  }
}
