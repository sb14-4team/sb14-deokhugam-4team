package com.sprint.deokhugam.book.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.domain.book.exception.BookErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class IsbnTest {

  // ===== (1) normalize() : 정리 =====
  // (1-1)
  @Test
  @DisplayName("13자리 숫자는 그대로 돌려준다")
  void normalize13() {
    assertThat(Isbn.normalize("9788966260959")).isEqualTo("9788966260959");
  }

  // (1-2)
  @Test
  @DisplayName("하이픈과 공백은 빼고 돌려준다")
  void normalizeRemovesHyphensAndSpaces() {
    assertThat(Isbn.normalize("978-89-6626-095-9")).isEqualTo("9788966260959");
    assertThat(Isbn.normalize("9 788966 260959")).isEqualTo("9788966260959");
    assertThat(Isbn.normalize("  9788966260959  ")).isEqualTo("9788966260959");
  }

  // (1-3)
  @Test
  @DisplayName("10자리는 같은 책의 13자리로 바꾼다")
  void normalize10To13() {
    assertThat(Isbn.normalize("8996991341")).isEqualTo("9788996991342");
    assertThat(Isbn.normalize("89-96991-34-1")).isEqualTo("9788996991342");
  }

  // (1-4)
  @Test
  @DisplayName("10자리 끝자리 X는 대소문자 모두 받는다")
  void normalize10WithX() {
    assertThat(Isbn.normalize("080442957X")).isEqualTo("9780804429573");
    assertThat(Isbn.normalize("080442957x")).isEqualTo("9780804429573");
  }

  // (1-5)
  @Test
  @DisplayName("null, 빈 문자열, 공백만 있으면 null을 돌려준다")
  void normalizeBlankToNull() {
    assertThat(Isbn.normalize(null)).isNull();
    assertThat(Isbn.normalize("")).isNull();
    assertThat(Isbn.normalize("   ")).isNull();
  }

  // ===== (2) normalize() : 검사 =====
  // (2-1)
  @ParameterizedTest
  @ValueSource(
      strings = {
        "9788966260950", // 13자리 체크섬 틀림
        "8996991340", // 10자리 체크섬 틀림
        "12345678901", // 11자리
        "978896626095", // 12자리
        "9768966260959", // 978, 979로 시작하지 않음
        "978896626095X", // 13자리에는 X가 없다
        "ISBN9788966260959", // 숫자가 아닌 글자
        "8966260950 9788966260959" // 두 개가 붙어 있음
      })
  @DisplayName("형식이나 체크섬이 틀리면 INVALID_ISBN 예외가 발생한다")
  void normalizeInvalid(String raw) {
    assertThatThrownBy(() -> Isbn.normalize(raw))
        .isInstanceOf(BusinessException.class)
        .extracting("errorCode")
        .isEqualTo(BookErrorCode.INVALID_ISBN);
  }

  // ===== (3) isValid() =====
  // (3-1)
  @Test
  @DisplayName("isValid는 예외 대신 true, false로 알려준다")
  void isValid() {
    assertThat(Isbn.isValid("978-89-6626-095-9")).isTrue();
    assertThat(Isbn.isValid("9788966260950")).isFalse();
  }
}
