package com.sprint.deokhugam.book;

import com.sprint.deokhugam.book.application.dto.BookDto;
import com.sprint.deokhugam.book.application.dto.BookSearchCondition;
import com.sprint.deokhugam.book.application.required.BookSearchPort;
import com.sprint.deokhugam.book.domain.Book;
import java.util.Comparator;
import java.util.List;

// BookSearchAdapter(Querydsl)가 DB에서 하는 일을 자바 스트림으로 흉내 낸다
// 도서 목록은 FakeBookRepositoryPort가 들고 있으므로 같은 가짜 저장소를 넘겨받아 읽는다
public class FakeBookSearchPort implements BookSearchPort {

  private final FakeBookRepositoryPort bookRepositoryPort;

  public FakeBookSearchPort(FakeBookRepositoryPort bookRepositoryPort) {
    this.bookRepositoryPort = bookRepositoryPort;
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Book> search(BookSearchCondition condition, int size) {
    // 정렬 기준 값으로 먼저 비교하고, 같으면 createdAt으로 비교한다
    Comparator<Book> order =
        (a, b) -> {
          int byKey = sortKey(condition, a).compareTo(sortKey(condition, b));
          return byKey != 0 ? byKey : a.getCreatedAt().compareTo(b.getCreatedAt());
        };
    if (condition.isDescending()) {
      order = order.reversed();
    }
    return bookRepositoryPort.all().stream()
        .filter(book -> !book.isDeleted())
        .filter(book -> matchesKeyword(book, condition.keyword()))
        .filter(book -> isAfterCursor(book, condition))
        .sorted(order)
        .limit(size)
        .toList();
  }

  @Override
  public long countByKeyword(String keyword) {
    return bookRepositoryPort.all().stream()
        .filter(book -> !book.isDeleted())
        .filter(book -> matchesKeyword(book, keyword))
        .count();
  }

  private boolean matchesKeyword(Book book, String keyword) {
    if (keyword == null) {
      return true;
    }
    String lower = keyword.toLowerCase();
    return book.getTitle().toLowerCase().contains(lower)
        || book.getAuthor().toLowerCase().contains(lower)
        || (book.getIsbn() != null && book.getIsbn().contains(keyword));
  }

  // DESC: 정렬값 < cursor 또는 (정렬값 = cursor 이고 createdAt < after). ASC는 반대
  private boolean isAfterCursor(Book book, BookSearchCondition condition) {
    if (condition.isFirstPage()) {
      return true;
    }
    int byKey =
        sortKey(condition, book).compareTo(condition.sortField().parseCursor(condition.cursor()));
    int byCreatedAt = book.getCreatedAt().compareTo(condition.after());
    if (condition.isDescending()) {
      return byKey < 0 || (byKey == 0 && byCreatedAt < 0);
    }
    return byKey > 0 || (byKey == 0 && byCreatedAt > 0);
  }

  // 정렬 기준 값(제목·출간일·평점·리뷰 수). 타입이 네 가지라 Comparable로 묶어서 비교만 한다
  @SuppressWarnings({"unchecked", "rawtypes"})
  private static Comparable sortKey(BookSearchCondition condition, Book book) {
    return (Comparable) condition.sortField().cursorOf(BookDto.from(book));
  }
}
