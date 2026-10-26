package com.sprint.deokhugam.book.adapter.api.kakao;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.sprint.deokhugam.book.application.dto.BookInfoDto;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

// 카카오 서버 대신 MockRestServiceServer가 응답한다. 실제 네트워크는 쓰지 않는다
class KakaoBookProviderAdapterTest {

  private static final String SEARCH_URL =
      "https://dapi.kakao.com/v3/search/book?target=isbn&query=9788966260959";

  private final RestClient.Builder builder =
      RestClient.builder()
          .baseUrl("https://dapi.kakao.com")
          .defaultHeader("Authorization", "KakaoAK test-key");
  private final MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
  private final KakaoBookProviderAdapter adapter = new KakaoBookProviderAdapter(builder.build());

  // 카카오 실제 응답 모양. price처럼 안 쓰는 필드도 섞어서 무시되는지 본다
  private static String kakaoJson(String thumbnail) {
    return """
        {
          "meta": {"is_end": true, "pageable_count": 1, "total_count": 1},
          "documents": [{
            "title": "클린 코드",
            "contents": "소개",
            "publisher": "인사이트",
            "authors": ["로버트 C. 마틴", "박재호"],
            "datetime": "2013-12-24T00:00:00.000+09:00",
            "isbn": "8966260950 9788966260959",
            "thumbnail": "%s",
            "price": 33000,
            "status": "정상판매"
          }]
        }
        """
        .formatted(thumbnail);
  }

  @Test
  @DisplayName("카카오 응답을 BookInfoDto로 바꾼다. 저자는 쉼표로 합치고 ISBN은 13자리를 고른다")
  void findByIsbn_mapsDocument() {
    server
        .expect(requestTo(SEARCH_URL))
        .andExpect(header("Authorization", "KakaoAK test-key"))
        .andRespond(withSuccess(kakaoJson(""), MediaType.APPLICATION_JSON));

    Optional<BookInfoDto> result = adapter.findByIsbn("9788966260959");

    assertThat(result).isPresent();
    BookInfoDto info = result.get();
    assertThat(info.title()).isEqualTo("클린 코드");
    assertThat(info.author()).isEqualTo("로버트 C. 마틴, 박재호");
    assertThat(info.description()).isEqualTo("소개");
    assertThat(info.publisher()).isEqualTo("인사이트");
    assertThat(info.publishedDate()).isEqualTo(LocalDate.of(2013, 12, 24));
    assertThat(info.isbn()).isEqualTo("9788966260959");
    assertThat(info.thumbnailImage()).isNull();
    server.verify();
  }

  @Test
  @DisplayName("documents가 비어 있으면 빈 Optional을 돌려준다")
  void findByIsbn_emptyDocuments() {
    server
        .expect(requestTo(SEARCH_URL))
        .andRespond(withSuccess("{\"documents\": []}", MediaType.APPLICATION_JSON));

    assertThat(adapter.findByIsbn("9788966260959")).isEmpty();
  }

  @Test
  @DisplayName("카카오가 5xx를 주면 예외를 잡지 않고 그대로 올린다")
  void findByIsbn_serverError() {
    server.expect(requestTo(SEARCH_URL)).andRespond(withServerError());

    assertThatThrownBy(() -> adapter.findByIsbn("9788966260959"))
        .isInstanceOf(RestClientException.class);
  }

  @Test
  @DisplayName("썸네일을 받지 못하면 thumbnailImage만 null이고 책 정보는 그대로 온다")
  void findByIsbn_thumbnailDownloadFails() {
    // 아무도 듣지 않는 포트라 연결이 바로 거절된다
    server
        .expect(requestTo(SEARCH_URL))
        .andRespond(
            withSuccess(kakaoJson("http://127.0.0.1:1/cover.jpg"), MediaType.APPLICATION_JSON));

    Optional<BookInfoDto> result = adapter.findByIsbn("9788966260959");

    assertThat(result).isPresent();
    assertThat(result.get().title()).isEqualTo("클린 코드");
    assertThat(result.get().thumbnailImage()).isNull();
  }
}
