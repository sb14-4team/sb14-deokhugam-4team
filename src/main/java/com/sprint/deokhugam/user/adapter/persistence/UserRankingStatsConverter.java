package com.sprint.deokhugam.user.adapter.persistence;

import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.common.exception.CommonErrorCode;
import com.sprint.deokhugam.user.domain.ranking.UserRankingStats;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Converter(autoApply = true)
@RequiredArgsConstructor
public class UserRankingStatsConverter implements AttributeConverter<UserRankingStats, String> {

  private final JsonMapper jsonMapper;

  @Override
  public String convertToDatabaseColumn(UserRankingStats attribute) {
    if (attribute == null) {
      return null;
    }

    try {
      return jsonMapper.writeValueAsString(attribute);
    } catch (JacksonException e) {
      throw new BusinessException(CommonErrorCode.INTERNAL_ERROR, "유저 순위 통계 직렬화 실패", e);
    }
  }

  @Override
  public UserRankingStats convertToEntityAttribute(String dbData) {
    if (dbData == null) {
      return null;
    }

    try {
      return jsonMapper.readValue(dbData, UserRankingStats.class);
    } catch (JacksonException e) {
      throw new BusinessException(CommonErrorCode.INTERNAL_ERROR, "유저 순위 통계 역직렬화 실패", e);
    }
  }
}
