package com.sprint.deokhugam.book.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class IsbnExtractorTest {

  private final IsbnExtractor extractor = new IsbnExtractor();

  @Test
  @DisplayName("'ISBN' 줄과 바코드 밑 숫자가 같은 값이면 그 값을 숫자만 남겨 돌려준다")
  void extract_sameValueTwice() {
    String text = "ISBN 978-89-6626-095-9\n9 788966 260959   88000";

    assertThat(extractor.extract(text)).contains(new Isbn("9788966260959"));
  }

  @Test
  @DisplayName("같은 값이 없으면 글 순서대로 첫 번째 후보")
  void extract_firstWhenNoDuplicate() {
    String text = "978-89-6626-095-9 그리고 9791162243077";

    assertThat(extractor.extract(text)).contains(new Isbn("9788966260959"));
  }

  @Test
  @DisplayName("두 번 나온 값이 뒤에 있어도 첫 번째보다 우선한다")
  void extract_duplicateBeatsFirst() {
    String text = "9791162243077\n978-89-6626-095-9\n9 788966 260959";

    assertThat(extractor.extract(text)).contains(new Isbn("9788966260959"));
  }

  @Test
  @DisplayName("긴 숫자열 안의 978은 후보가 아니다")
  void extract_ignoresLongerDigitRun() {
    String text = "주문번호 19788966260959123";

    assertThat(extractor.extract(text)).isEmpty();
  }

  @Test
  @DisplayName("체크섬이 틀린 후보는 건너뛰고 다음 후보를 고른다")
  void extract_skipsInvalidChecksum() {
    String text = "ISBN 978-89-6626-095-0\n9791162243077";

    assertThat(extractor.extract(text)).contains(new Isbn("9791162243077"));
  }

  @Test
  @DisplayName("후보가 모두 체크섬이 틀리면 빈 Optional")
  void extract_allInvalidChecksum() {
    assertThat(extractor.extract("9788966260950 9788966260951")).isEmpty();
  }

  @Test
  @DisplayName("978, 979로 시작하지 않는 숫자나 빈 글이면 빈 Optional")
  void extract_noCandidate() {
    assertThat(extractor.extract("가격 88000원 전화 010-1234-5678")).isEmpty();
    assertThat(extractor.extract("")).isEmpty();
  }
}
