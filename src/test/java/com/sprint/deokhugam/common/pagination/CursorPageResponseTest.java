package com.sprint.deokhugam.common.pagination;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CursorPageResponseTest {

  record Item(String title, double rating, Instant createdAt) {}

  private static final Instant T1 = Instant.parse("2026-10-20T00:00:01Z");
  private static final Instant T2 = Instant.parse("2026-10-20T00:00:02Z");
  private static final Instant T3 = Instant.parse("2026-10-20T00:00:03Z");

  @Test
  @DisplayName("limit보다 많이 조회되면 limit개만 담고 마지막 요소로 다음 커서를 만든다")
  void hasNext() {
    List<Item> rows =
        List.of(new Item("a", 1.0, T1), new Item("b", 2.0, T2), new Item("c", 3.0, T3));

    CursorPageResponse<Item> response =
        CursorPageResponse.of(rows, 2, Item::title, Item::createdAt, 10);

    assertThat(response.content()).containsExactly(rows.get(0), rows.get(1));
    assertThat(response.hasNext()).isTrue();
    assertThat(response.nextCursor()).isEqualTo("b");
    assertThat(response.nextAfter()).isEqualTo(T2);
    assertThat(response.size()).isEqualTo(2);
    assertThat(response.totalElements()).isEqualTo(10);
  }

  @Test
  @DisplayName("limit 이하로 조회되면 다음 페이지가 없다")
  void lastPage() {
    List<Item> rows = List.of(new Item("a", 1.0, T1), new Item("b", 2.0, T2));

    CursorPageResponse<Item> response =
        CursorPageResponse.of(rows, 2, Item::title, Item::createdAt, 2);

    assertThat(response.content()).hasSize(2);
    assertThat(response.hasNext()).isFalse();
    assertThat(response.nextCursor()).isNull();
    assertThat(response.nextAfter()).isNull();
  }

  @Test
  @DisplayName("숫자로 정렬해도 커서는 문자열이다")
  void numberCursor() {
    List<Item> rows = List.of(new Item("a", 4.5, T1), new Item("b", 3.0, T2));

    CursorPageResponse<Item> response =
        CursorPageResponse.of(rows, 1, Item::rating, Item::createdAt, 2);

    assertThat(response.nextCursor()).isEqualTo("4.5");
  }
}
