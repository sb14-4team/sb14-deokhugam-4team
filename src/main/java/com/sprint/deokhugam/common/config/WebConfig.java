package com.sprint.deokhugam.common.config;

import com.sprint.deokhugam.common.web.resolver.RequestArgumentResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverters;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tools.jackson.databind.json.JsonMapper;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

  private final RequestArgumentResolver requestArgumentResolver;
  private final JsonMapper jsonMapper;

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(requestArgumentResolver);
  }

  // Swagger UI처럼 JSON 파트에 Content-Type이 없으면 octet-stream으로 오는데, 그것도 JSON으로 읽는다
  @Override
  public void configureMessageConverters(HttpMessageConverters.ServerBuilder builder) {
    JacksonJsonHttpMessageConverter octetStreamAsJson =
        new JacksonJsonHttpMessageConverter(jsonMapper) {
          @Override
          public boolean canWrite(ResolvableType type, Class<?> clazz, MediaType mediaType) {
            return false;
          }
        };
    octetStreamAsJson.setSupportedMediaTypes(List.of(MediaType.APPLICATION_OCTET_STREAM));
    builder.addCustomConverter(octetStreamAsJson);
  }
}
