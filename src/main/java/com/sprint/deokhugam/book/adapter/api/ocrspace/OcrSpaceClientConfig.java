package com.sprint.deokhugam.book.adapter.api.ocrspace;

import java.net.http.HttpClient;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class OcrSpaceClientConfig {

  // 키는 .env의 OCR_SPACE_API_KEY를 application.yaml에서 ocr-space.api-key로 읽는다
  @Bean
  public RestClient ocrSpaceClient(@Value("${ocr-space.api-key}") String apiKey) {
    // OCR은 몇 초 걸릴 수 있어서 연결은 3초, 응답은 10초까지만 기다린다
    HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(3)).build();
    JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(httpClient);
    factory.setReadTimeout(Duration.ofSeconds(10));

    return RestClient.builder()
        .baseUrl("https://api.ocr.space")
        .defaultHeader("apikey", apiKey)
        .requestFactory(factory)
        .build();
  }
}
