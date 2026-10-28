package com.sprint.deokhugam.book.adapter.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.sprint.deokhugam.book.application.dto.BookDto;
import com.sprint.deokhugam.book.application.dto.BookSearchCondition;
import com.sprint.deokhugam.book.application.dto.BookSortField;
import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.book.domain.Isbn;
import com.sprint.deokhugam.common.config.JpaConfig;
import com.sprint.deokhugam.common.config.QuerydslConfig;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

// BookSearchAdapter가 만드는 SQL만 검증한다. 컨트롤러·서비스는 띄우지 않는다
@DataJpaTest // JPA 관련 빈만 띄우는 가벼운 테스트. 각 테스트는 트랜잭션 안에서 돌고 끝나면 롤백된다
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // H2로 바꾸지 않는다
@ActiveProfiles("test") // application-test.yaml의 PostgreSQL(deokhugam_test)을 쓴다
@Import({QuerydslConfig.class, JpaConfig.class, BookSearchAdapter.class}) // 자동으로 안 띄우는 빈
class BookSearchAdapterTest {

  @Autowired private EntityManager entityManager;
  @Autowired private BookSearchAdapter bookSearchAdapter;

  // 저장 순서대로 1초씩 늦게 등록된 것으로 본다 → 동률일 때 createdAt 순서를 예측할 수 있다
  private static final Instant BASE_TIME = Instant.parse("2026-01-01T00:00:00Z");
  private int savedCount = 0;

  private static Instant at(int seconds) {
    return BASE_TIME.plusSeconds(seconds);
  }

  // createdAt은 JPA Auditing이 저장 시각으로 채우는데, 연달아 저장하면 같은 시각이 나올 수 있다
  // 동률 순서를 예측하려고 저장한 뒤 UPDATE로 createdAt을 BASE_TIME + n초로 바꿔 둔다
  private Book saved(
      String title, String author, String isbn, LocalDate publishedDate, int... scores) {
    Book book = Book.create(title, author, "소개", "출판사", publishedDate, Isbn.of(isbn), null);
    for (int score : scores) {
      book.addReview(score);
    }
    entityManager.persist(book);
    entityManager.flush();
    entityManager
        .createQuery("update Book b set b.createdAt = :createdAt where b.id = :id")
        .setParameter("createdAt", at(savedCount++))
        .setParameter("id", book.getId())
        .executeUpdate();
    return book;
  }

  private Book saved(String title) {
    return saved(title, "저자", null, LocalDate.of(2020, 1, 1));
  }

  // 영속성 컨텍스트를 비워서 다음 조회가 반드시 SQL을 치고, UPDATE로 바꾼 createdAt을 읽어 오게 한다
  private void flushAndClear() {
    entityManager.flush();
    entityManager.clear();
  }

  private List<Book> search(
      String keyword, BookSortField field, Sort.Direction dir, String cursor, Instant after) {
    return bookSearchAdapter.search(
        new BookSearchCondition(keyword, field, dir, cursor, after), 100);
  }

  private List<Book> search(BookSortField field, Sort.Direction dir) {
    return search(null, field, dir, null, null);
  }

  // 한 페이지에 size권씩, 커서로 끝까지 넘기면서 나온 순서대로 모은다 (중복·누락 검증용)
  private List<Book> collectAllPages(BookSortField field, Sort.Direction dir, int size) {
    List<Book> collected = new ArrayList<>();
    String cursor = null;
    Instant after = null;
    while (true) {
      List<Book> page =
          bookSearchAdapter.search(new BookSearchCondition(null, field, dir, cursor, after), size);
      collected.addAll(page);
      if (page.size() < size) {
        return collected;
      }
      Book last = page.get(page.size() - 1);
      cursor = String.valueOf(field.cursorOf(BookDto.from(last)));
      after = last.getCreatedAt();
    }
  }

  // ===== (1) search() : 키워드 =====

  // (1-1)
  @Test
  @DisplayName("키워드가 없으면 전체를 돌려주고 논리 삭제된 도서는 뺀다")
  void searchWithoutKeyword() {
    saved("가");
    saved("나");
    saved("다").softDelete();
    flushAndClear();

    List<Book> result = search(BookSortField.TITLE, Sort.Direction.ASC);

    assertThat(result).extracting(Book::getTitle).containsExactly("가", "나");
  }

  // (1-2)
  @Test
  @DisplayName("키워드는 제목·저자에 대소문자 없이, ISBN에 그대로 부분 일치한다")
  void searchByKeyword() {
    saved("Clean Code", "Robert C. Martin", "9788966260959", LocalDate.of(2013, 12, 24));
    saved("리팩터링", "마틴 파울러", "9791162242742", LocalDate.of(2020, 4, 1));
    saved("자바 ORM", "김영한", null, LocalDate.of(2015, 7, 28));
    flushAndClear();

    assertThat(search("CLEAN", BookSortField.TITLE, Sort.Direction.ASC, null, null))
        .extracting(Book::getTitle)
        .containsExactly("Clean Code");
    assertThat(search("martin", BookSortField.TITLE, Sort.Direction.ASC, null, null))
        .extracting(Book::getTitle)
        .containsExactly("Clean Code");
    assertThat(search("마틴", BookSortField.TITLE, Sort.Direction.ASC, null, null))
        .extracting(Book::getTitle)
        .containsExactly("리팩터링");
    assertThat(search("2742", BookSortField.TITLE, Sort.Direction.ASC, null, null))
        .extracting(Book::getTitle)
        .containsExactly("리팩터링");
    assertThat(search("없는책", BookSortField.TITLE, Sort.Direction.ASC, null, null)).isEmpty();
  }

  // (1-3)
  @Test
  @DisplayName("키워드에 맞아도 논리 삭제된 도서는 나오지 않는다")
  void searchExcludesDeletedEvenIfKeywordMatches() {
    saved("클린 코드");
    saved("클린 아키텍처").softDelete();
    flushAndClear();

    List<Book> result = search("클린", BookSortField.TITLE, Sort.Direction.ASC, null, null);

    assertThat(result).extracting(Book::getTitle).containsExactly("클린 코드");
  }

  // ===== (2) countByKeyword() =====

  // (2-1)
  @Test
  @DisplayName("키워드에 맞는 도서 수를 세고, 키워드가 없으면 전체를, 논리 삭제는 뺀다")
  void countByKeyword() {
    saved("클린 코드");
    saved("클린 아키텍처");
    saved("리팩터링");
    saved("클린 코더").softDelete();
    flushAndClear();

    assertThat(bookSearchAdapter.countByKeyword("클린")).isEqualTo(2);
    assertThat(bookSearchAdapter.countByKeyword(null)).isEqualTo(3);
    assertThat(bookSearchAdapter.countByKeyword("없는책")).isZero();
  }

  // ===== (3) search() : 정렬 =====

  // (3-1)
  @Test
  @DisplayName("DESC는 정렬값 큰 순, 같으면 나중에 등록된 순이다")
  void orderByDesc() {
    saved("가"); // at(0)
    saved("나"); // at(1)
    saved("나"); // at(2)
    saved("다"); // at(3)
    flushAndClear();

    List<Book> result = search(BookSortField.TITLE, Sort.Direction.DESC);

    assertThat(result)
        .extracting(Book::getTitle, Book::getCreatedAt)
        .containsExactly(
            tuple("다", at(3)), tuple("나", at(2)), tuple("나", at(1)), tuple("가", at(0)));
  }

  // (3-2)
  @Test
  @DisplayName("ASC는 정렬값 작은 순, 같으면 먼저 등록된 순이다")
  void orderByAsc() {
    saved("나"); // at(0)
    saved("다"); // at(1)
    saved("나"); // at(2)
    saved("가"); // at(3)
    flushAndClear();

    List<Book> result = search(BookSortField.TITLE, Sort.Direction.ASC);

    assertThat(result)
        .extracting(Book::getTitle, Book::getCreatedAt)
        .containsExactly(
            tuple("가", at(3)), tuple("나", at(0)), tuple("나", at(2)), tuple("다", at(1)));
  }

  // (3-3)
  @Test
  @DisplayName("size만큼만 돌려준다")
  void limit() {
    saved("가");
    saved("나");
    saved("다");
    flushAndClear();

    List<Book> result =
        bookSearchAdapter.search(
            new BookSearchCondition(null, BookSortField.TITLE, Sort.Direction.ASC, null, null), 2);

    assertThat(result).extracting(Book::getTitle).containsExactly("가", "나");
  }

  // ===== (4) search() : 커서 (정렬 기준별 WHERE 조건) =====
  // 한 페이지 1권으로 끝까지 넘기면 커서 경계마다 WHERE가 실행된다. 동률 구간에서 createdAt 2차 조건도 검증된다

  // (4-1)
  @Test
  @DisplayName("제목 커서: 같은 제목이 있어도 중복·누락 없이 이어진다")
  void cursorByTitle() {
    saved("가"); // at(0)
    saved("나"); // at(1)
    saved("나"); // at(2)
    saved("다"); // at(3)
    flushAndClear();

    assertThat(collectAllPages(BookSortField.TITLE, Sort.Direction.DESC, 1))
        .extracting(Book::getTitle, Book::getCreatedAt)
        .containsExactly(
            tuple("다", at(3)), tuple("나", at(2)), tuple("나", at(1)), tuple("가", at(0)));
    assertThat(collectAllPages(BookSortField.TITLE, Sort.Direction.ASC, 1))
        .extracting(Book::getTitle, Book::getCreatedAt)
        .containsExactly(
            tuple("가", at(0)), tuple("나", at(1)), tuple("나", at(2)), tuple("다", at(3)));
  }

  // (4-2)
  @Test
  @DisplayName("출간일 커서: 같은 출간일이 있어도 중복·누락 없이 이어진다")
  void cursorByPublishedDate() {
    saved("A", "저자", null, LocalDate.of(2020, 1, 1)); // at(0)
    saved("B", "저자", null, LocalDate.of(2021, 1, 1)); // at(1)
    saved("C", "저자", null, LocalDate.of(2021, 1, 1)); // at(2)
    saved("D", "저자", null, LocalDate.of(2022, 1, 1)); // at(3)
    flushAndClear();

    assertThat(collectAllPages(BookSortField.PUBLISHED_DATE, Sort.Direction.DESC, 1))
        .extracting(Book::getTitle)
        .containsExactly("D", "C", "B", "A");
    assertThat(collectAllPages(BookSortField.PUBLISHED_DATE, Sort.Direction.ASC, 1))
        .extracting(Book::getTitle)
        .containsExactly("A", "B", "C", "D");
  }

  // (4-3)
  @Test
  @DisplayName("평점 커서: 같은 평점이 있어도 중복·누락 없이 이어진다")
  void cursorByRating() {
    saved("A", "저자", null, LocalDate.of(2020, 1, 1), 3); // 3.0, at(0)
    saved("B", "저자", null, LocalDate.of(2020, 1, 1), 5); // 5.0, at(1)
    saved("C", "저자", null, LocalDate.of(2020, 1, 1), 5); // 5.0, at(2)
    saved("D", "저자", null, LocalDate.of(2020, 1, 1), 1); // 1.0, at(3)
    flushAndClear();

    assertThat(collectAllPages(BookSortField.RATING, Sort.Direction.DESC, 1))
        .extracting(Book::getTitle)
        .containsExactly("C", "B", "A", "D");
    assertThat(collectAllPages(BookSortField.RATING, Sort.Direction.ASC, 1))
        .extracting(Book::getTitle)
        .containsExactly("D", "A", "B", "C");
  }

  // (4-4)
  @Test
  @DisplayName("리뷰 수 커서: 같은 리뷰 수가 있어도 중복·누락 없이 이어진다")
  void cursorByReviewCount() {
    saved("A", "저자", null, LocalDate.of(2020, 1, 1), 5); // 1개, at(0)
    saved("B", "저자", null, LocalDate.of(2020, 1, 1), 5, 5); // 2개, at(1)
    saved("C", "저자", null, LocalDate.of(2020, 1, 1), 5, 5); // 2개, at(2)
    saved("D", "저자", null, LocalDate.of(2020, 1, 1), 5, 5, 5); // 3개, at(3)
    flushAndClear();

    assertThat(collectAllPages(BookSortField.REVIEW_COUNT, Sort.Direction.DESC, 1))
        .extracting(Book::getTitle)
        .containsExactly("D", "C", "B", "A");
    assertThat(collectAllPages(BookSortField.REVIEW_COUNT, Sort.Direction.ASC, 1))
        .extracting(Book::getTitle)
        .containsExactly("A", "B", "C", "D");
  }

  // (4-5)
  @Test
  @DisplayName("키워드와 커서를 같이 주면 키워드에 맞는 것 중에서 커서 다음부터 돌려준다")
  void cursorWithKeyword() {
    saved("클린 가"); // at(0)
    saved("리팩터링"); // at(1)
    saved("클린 나"); // at(2)
    saved("클린 다"); // at(3)
    flushAndClear();

    // 제목 DESC: 클린 다 > 클린 나 > 클린 가. "클린 다" 다음부터
    List<Book> result = search("클린", BookSortField.TITLE, Sort.Direction.DESC, "클린 다", at(3));

    assertThat(result).extracting(Book::getTitle).containsExactly("클린 나", "클린 가");
  }
}
