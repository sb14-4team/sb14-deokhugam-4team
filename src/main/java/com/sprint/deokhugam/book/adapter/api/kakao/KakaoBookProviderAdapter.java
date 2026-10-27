package com.sprint.deokhugam.book.adapter.api.kakao;

import com.sprint.deokhugam.book.application.dto.BookInfoDto;
import com.sprint.deokhugam.book.application.required.BookProviderPort;
import java.net.URI;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoBookProviderAdapter implements BookProviderPort {

  private final RestClient kakaoClient;

  // 썸네일은 카카오 API가 아닌 이미지 서버에서 받으므로 인증 헤더 없는 클라이언트를 따로 쓴다
  private final RestClient imageClient = RestClient.create();

  @Override
  public Optional<BookInfoDto> findByIsbn(String isbn) {
    return fetchDocument(isbn).map(doc -> toDto(doc, downloadThumbnail(doc.thumbnail())));
  }

  // 카카오 호출 실패(4xx·5xx·끊김)는 잡지 않는다. 책 정보가 없으면 돌려줄 게 없으니 500이 맞다
  private Optional<KakaoBookResponse.Document> fetchDocument(String isbn) {
    KakaoBookResponse response =
        kakaoClient
            .get()
            .uri("/v3/search/book?target=isbn&query={isbn}", isbn)
            .retrieve()
            .body(KakaoBookResponse.class);
    if (response == null || response.documents().isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(response.documents().getFirst());
  }

  // 표지는 없어도 되는 정보라 받기에 실패하면 null로 두고 책 정보는 살린다
  private byte[] downloadThumbnail(String url) {
    if (url == null || url.isBlank()) {
      return null;
    }
    try {
      // 카카오 URL은 fname 값이 이미 인코딩돼 있다. 문자열로 넘기면 한 번 더 인코딩돼 403이 난다
      return imageClient.get().uri(URI.create(url)).retrieve().body(byte[].class);
    } catch (RestClientException e) {
      log.warn("썸네일 받기 실패: {} ({})", url, e.getMessage());
      return null;
    }
  }

  private BookInfoDto toDto(KakaoBookResponse.Document doc, byte[] thumbnail) {
    return new BookInfoDto(
        doc.title(),
        doc.authors() == null ? null : String.join(", ", doc.authors()),
        doc.contents(),
        doc.publisher(),
        toDate(doc.datetime()),
        pickIsbn13(doc.isbn()),
        thumbnail);
  }

  private LocalDate toDate(String datetime) {
    if (datetime == null || datetime.isBlank()) {
      return null;
    }
    return OffsetDateTime.parse(datetime).toLocalDate();
  }

  // 카카오 isbn은 "10자리 13자리"가 공백으로 붙어 온다. 마지막 값이 13자리
  private String pickIsbn13(String isbn) {
    if (isbn == null || isbn.isBlank()) {
      return null;
    }
    String[] parts = isbn.split(" ");
    return parts[parts.length - 1];
  }
}
