package com.sprint.deokhugam.common.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
public class MdcLoggingFilter extends OncePerRequestFilter {

  private static final String REQUEST_ID = "requestId";
  private static final String CLIENT_IP = "clientIp";

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String requestId = UUID.randomUUID().toString().replace("-", "");
    String clientIp = request.getRemoteAddr();
    long startedAt = System.nanoTime();

    try {
      MDC.put(REQUEST_ID, requestId);
      MDC.put(CLIENT_IP, clientIp);
      MDC.put("requestMethod", request.getMethod());
      MDC.put("requestUri", request.getRequestURI());

      response.setHeader(REQUEST_ID, requestId);
      response.setHeader(CLIENT_IP, clientIp);
      log.info("요청 시작 ###");
      filterChain.doFilter(request, response);
    } catch (IOException | ServletException | RuntimeException e) {
      log.error("요청 처리 중 미처리 예외", e);
      throw e;
    } finally {
      try {
        long duration = (System.nanoTime() - startedAt) / 1_000_000;

        log.info("요청 종료: 소요시간 = {}ms", duration);
      } finally {
        MDC.remove(REQUEST_ID);
        MDC.remove(CLIENT_IP);
        MDC.remove("requestMethod");
        MDC.remove("requestUri");
      }
    }
  }
}
