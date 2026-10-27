package com.sprint.deokhugam.book.domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

// OCR로 읽은 글에서 ISBN 하나를 골라낸다
@Component
public class IsbnExtractor {

  // 978 또는 979로 시작하는 13자리 숫자. 중간에 하이픈이나 공백이 끼어도 된다.
  // 바코드 아래 숫자는 "9 788966 260959"처럼 첫 자리 뒤에 공백이 있어서 앞부분에도 허용한다.
  // 앞이나 뒤에 숫자가 더 붙어 있으면(주문번호 같은 긴 숫자) ISBN이 아니다.
  private static final Pattern ISBN13 =
      Pattern.compile("(?<!\\d)9[- ]?7[- ]?[89](?:[- ]?\\d){10}(?!\\d)");

  public Optional<String> extract(String text) {
    List<String> candidates = findCandidates(text);
    return pickOne(candidates).map(Isbn::normalize);
  }

  // 글에 나온 순서대로 ISBN 모양의 문자열을 모두 찾는다. 숫자를 잘못 읽어 체크섬이 틀린 것은 뺀다
  private List<String> findCandidates(String text) {
    return ISBN13.matcher(text).results().map(MatchResult::group).filter(Isbn::isValid).toList();
  }

  // 책 뒷면에는 "ISBN 978-..." 글자와 바코드 아래 숫자가 같이 찍혀 있다.
  // 둘이 같은 값이면 제대로 읽은 것이니 그 값을 고르고, 아니면 먼저 나온 것을 고른다.
  private Optional<String> pickOne(List<String> candidates) {
    // 숫자만 남긴 값이 같은 것끼리 묶는다. LinkedHashMap이라 글에 나온 순서가 유지된다
    Map<String, List<String>> byValue =
        candidates.stream()
            .collect(
                Collectors.groupingBy(Isbn::normalize, LinkedHashMap::new, Collectors.toList()));

    return byValue.values().stream()
        .filter(same -> same.size() >= 2) // 두 번 이상 나온 값
        .map(List::getFirst)
        .findFirst()
        .or(() -> candidates.stream().findFirst()); // 없으면 첫 번째 후보
  }
}
