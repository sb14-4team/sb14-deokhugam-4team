package com.sprint.deokhugam.book.adapter.api.ocrspace;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** OCR Space 응답. 쓰는 필드만 담고 나머지는 Jackson이 무시한다. JSON 키가 대문자라 @JsonProperty로 잇는다. */
public record OcrSpaceResponse(
    @JsonProperty("ParsedResults") List<ParsedResult> parsedResults,
    @JsonProperty("OCRExitCode") String ocrExitCode, // 숫자로도 문자열로도 와서 String으로 받는다
    @JsonProperty("IsErroredOnProcessing") boolean erroredOnProcessing,
    @JsonProperty("ErrorMessage") Object errorMessage) { // 문자열 하나 또는 배열로 온다

  public record ParsedResult(@JsonProperty("ParsedText") String parsedText) {}

  // OCRExitCode는 1 성공, 2 일부 성공, 3 전부 실패, 4 오류
  public boolean failed() {
    return erroredOnProcessing || "3".equals(ocrExitCode) || "4".equals(ocrExitCode);
  }
}
