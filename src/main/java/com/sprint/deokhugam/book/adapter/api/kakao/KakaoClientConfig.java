package com.sprint.deokhugam.book.adapter.api.kakao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class KakaoClientConfig {

  // 키는 .env의 KAKAO_REST_API_KEY가 application.yaml의 kakao.rest-api-key로 들어온다
  @Bean
  public RestClient kakaoClient(@Value("${kakao.rest-api-key}") String restApiKey) {
    return RestClient.builder()
        .baseUrl("https://dapi.kakao.com")
        .defaultHeader("Authorization", "KakaoAK " + restApiKey)
        .build();
  }
}
