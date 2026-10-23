package com.sprint.deokhugam.book;

import com.sprint.deokhugam.book.application.dto.BookDto;
import com.sprint.deokhugam.book.application.dto.BookSearchCondition;
import com.sprint.deokhugam.book.application.required.BookPersistencePort;
import com.sprint.deokhugam.book.domain.Book;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.springframework.test.util.ReflectionTestUtils;

public class FakeBookPersistencePort implements BookPersistencePort {

  // 저장 순서대로 1초씩 늦게 등록된 것으로 본다 → createdAt 정렬을 예측할 수 있다
  private static final Instant BASE_TIME = Instant.parse("2026-01-01T00:00:00Z");

  private final List<Book> books = new ArrayList<>();

  @Override
  public Book save(Book book) {
    if (book.getId() == null) {
      // 실제 DB가 없으니 JPA 대신 여기서 id와 createdAt을 채운다
      ReflectionTestUtils.setField(book, "id", UUID.randomUUID());
      ReflectionTestUtils.setField(book, "createdAt", BASE_TIME.plusSeconds(books.size()));
    }
    books.add(book);
    return book;
  }

  @Override
  public Optional<Book> findActiveById(UUID bookId) {
    return books.stream()
        .filter(book -> Objects.equals(book.getId(), bookId))
        .filter(book -> !book.isDeleted())
        .findFirst();
  }

  @Override
  public boolean existsByIsbn(String isbn) {
    return books.stream().anyMatch(book -> Objects.equals(book.getIsbn(), isbn));
  }

  // BookSearchRepository(Querydsl)가 DB에서 하는 일을 자바 스트림으로 흉내 낸다
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
    return books.stream()
        .filter(book -> !book.isDeleted())
        .filter(book -> matchesKeyword(book, condition.keyword()))
        .filter(book -> isAfterCursor(book, condition))
        .sorted(order)
        .limit(size)
        .toList();
  }

  @Override
  public long countByKeyword(String keyword) {
    return books.stream()
        .filter(book -> !book.isDeleted())
        .filter(book -> matchesKeyword(book, keyword))
        .count();
  }

  public int count() {
    return books.size();
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
