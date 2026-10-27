package com.sprint.deokhugam.book.adapter.persistence;

import com.sprint.deokhugam.book.domain.Isbn;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

// Isbn 값 객체를 DB에는 문자열 한 칸으로 넣고 꺼낸다. autoApply라 Isbn 타입 필드에 자동으로 붙는다
@Converter(autoApply = true)
public class IsbnConverter implements AttributeConverter<Isbn, String> {

  @Override
  public String convertToDatabaseColumn(Isbn isbn) {
    return isbn == null ? null : isbn.value();
  }

  @Override
  public Isbn convertToEntityAttribute(String dbData) {
    return dbData == null ? null : new Isbn(dbData);
  }
}
