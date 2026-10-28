package com.sprint.deokhugam.book.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.deokhugam.book.domain.Isbn;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class IsbnConverterTest {

  private final IsbnConverter converter = new IsbnConverter();

  @Test
  @DisplayName("DB에 넣을 때는 13자리 문자열, 꺼낼 때는 Isbn 값 객체다")
  void roundTrip() {
    assertThat(converter.convertToDatabaseColumn(new Isbn("978-89-6626-095-9")))
        .isEqualTo("9788966260959");
    assertThat(converter.convertToEntityAttribute("9788966260959"))
        .isEqualTo(new Isbn("9788966260959"));
  }

  @Test
  @DisplayName("ISBN이 없는 도서는 양쪽 다 null이다")
  void nullStaysNull() {
    assertThat(converter.convertToDatabaseColumn(null)).isNull();
    assertThat(converter.convertToEntityAttribute(null)).isNull();
  }
}
