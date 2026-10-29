package com.sprint.deokhugam.book.adapter.storage.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3ClientConfig {

  // 자격 증명은 SDK가 찾는다. 로컬은 aws login, 서버는 인스턴스 역할
  @Bean
  public S3Client s3Client(@Value("${aws.s3.region}") String region) {
    return S3Client.builder().region(Region.of(region)).build();
  }
}
