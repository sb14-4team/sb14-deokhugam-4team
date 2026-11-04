package com.sprint.deokhugam.common.config;

import com.sprint.deokhugam.common.exception.ErrorResponse;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import java.util.Map;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

  // Swagger 화면(/swagger-ui) 맨 위에 보이는 API 문서 제목·설명·버전
  @Bean
  public OpenAPI openApi() {
    return new OpenAPI()
        .info(new Info().title("덕후감 API").description("덕후감 프로젝트 API 명세서").version("v1.0.0"));
  }

  // 모든 4xx·5xx 응답 예시를 ErrorResponse 모양으로 바꾼다.
  // 이게 없으면 400 예시에 성공 응답(책 목록 등)이 보인다.
  // 도메인 개발자는 @ApiResponse(responseCode = "404", description = "...")처럼 설명만 쓰면 된다.
  @Bean
  public OpenApiCustomizer errorResponseCustomizer() {
    return openApi -> {
      if (openApi.getComponents() == null) {
        openApi.setComponents(new Components());
      }
      // ErrorResponse 모양을 문서의 Schemas 목록에 등록
      Map<String, Schema> schemas = ModelConverters.getInstance().read(ErrorResponse.class);
      schemas.forEach(openApi.getComponents()::addSchemas);

      if (openApi.getPaths() == null) {
        return;
      }
      for (PathItem pathItem : openApi.getPaths().values()) {
        for (Operation operation : pathItem.readOperations()) {
          for (Map.Entry<String, ApiResponse> entry : operation.getResponses().entrySet()) {
            String statusCode = entry.getKey();
            if (statusCode.startsWith("4") || statusCode.startsWith("5")) {
              entry.getValue().setContent(errorContent());
            }
          }
        }
      }
    };
  }

  // 위에서 등록한 ErrorResponse를 가리키는 응답 내용
  private Content errorContent() {
    Schema<?> errorSchema = new Schema<>().$ref("#/components/schemas/ErrorResponse");
    return new Content().addMediaType("application/json", new MediaType().schema(errorSchema));
  }
}
