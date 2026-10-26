package com.sprint.deokhugam.book.application;

import com.sprint.deokhugam.book.application.dto.BookDto;
import com.sprint.deokhugam.book.application.dto.BookInfoDto;
import com.sprint.deokhugam.book.application.dto.BookSearchCondition;
import com.sprint.deokhugam.book.application.provided.query.BookFinder;
import com.sprint.deokhugam.book.application.required.BookProviderPort;
import com.sprint.deokhugam.book.application.required.BookRepositoryPort;
import com.sprint.deokhugam.book.application.required.BookSearchPort;
import com.sprint.deokhugam.book.application.required.OcrTextPort;
import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.book.domain.IsbnExtractor;
import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.common.pagination.CursorPageResponse;
import com.sprint.deokhugam.domain.book.exception.BookErrorCode;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookQueryService implements BookFinder {

  private final BookRepositoryPort bookRepositoryPort;
  private final BookSearchPort bookSearchPort;
  private final BookProviderPort bookProviderPort;
  private final OcrTextPort ocrTextPort;
  private final IsbnExtractor isbnExtractor;

  private static final long MAX_IMAGE_BYTES = 1_000_000; // OCR Space 무료 한도 1MB
  private static final Set<String> IMAGE_TYPES = Set.of("image/jpeg", "image/png");
  private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png");

  @Override
  public Book findById(UUID bookId) {
    return bookRepositoryPort
        .findActiveById(bookId)
        .orElseThrow(() -> new BusinessException(BookErrorCode.NOT_FOUND, "bookId=" + bookId));
  }

  @Override
  public BookDto getById(UUID bookId) {
    return BookDto.from(findById(bookId));
  }

  @Override
  public CursorPageResponse<BookDto> search(BookSearchCondition condition, int limit) {
    // 다음 페이지가 있는지 알기 위해 limit보다 1개 더 가져온다. 넘치는 1개는 응답에서 잘린다
    List<BookDto> rows =
        bookSearchPort.search(condition, limit + 1).stream().map(BookDto::from).toList();
    long totalElements = bookSearchPort.countByKeyword(condition.keyword());

    // nextCursor = 마지막 도서의 정렬 기준 값, nextAfter = 마지막 도서의 createdAt
    return CursorPageResponse.of(
        rows, limit, condition.sortField()::cursorOf, BookDto::createdAt, totalElements);
  }

  // '없음'을 404로 바꾸는 건 우리 서비스의 규칙이라 어댑터가 아닌 여기서 한다
  @Override
  public BookInfoDto getBookInfo(String isbn) {
    return bookProviderPort
        .findByIsbn(isbn)
        .orElseThrow(() -> new BusinessException(BookErrorCode.INFO_NOT_FOUND, "isbn=" + isbn));
  }

  // 사진에서 글자를 읽고 그 안에서 ISBN을 찾는다. 글자나 ISBN이 없으면 400
  @Override
  public String recognizeIsbn(MultipartFile image) {
    validateImage(image);
    String filename = image.getOriginalFilename();

    String text;
    try {
      text = ocrTextPort.readText(image.getBytes(), filename, image.getContentType());
    } catch (IOException e) {
      // 업로드된 파일을 읽지 못하면 파일 쪽 문제라 400
      throw new BusinessException(BookErrorCode.INVALID_IMAGE, e);
    }

    if (text.isBlank()) {
      throw new BusinessException(BookErrorCode.OCR_FAILED, "글자를 읽지 못함, file=" + filename);
    }
    return isbnExtractor
        .extract(text)
        .orElseThrow(
            () -> new BusinessException(BookErrorCode.OCR_FAILED, "ISBN 없음, file=" + filename));
  }

  // OCR Space에 보내기 전에 파일을 먼저 확인한다. 무료 한도가 하루 500회라 헛된 호출을 줄인다
  private void validateImage(MultipartFile image) {
    if (image == null || image.isEmpty()) {
      throw new BusinessException(BookErrorCode.INVALID_IMAGE, "빈 파일");
    }
    if (image.getSize() > MAX_IMAGE_BYTES) {
      throw new BusinessException(BookErrorCode.INVALID_IMAGE, "크기 초과: " + image.getSize());
    }
    // jpg, png만 받는다. Content-Type과 확장자를 둘 다 본다
    String contentType = image.getContentType();
    String extension = StringUtils.getFilenameExtension(image.getOriginalFilename());
    boolean typeOk = contentType != null && IMAGE_TYPES.contains(contentType);
    boolean extensionOk = extension != null && IMAGE_EXTENSIONS.contains(extension.toLowerCase());
    if (!typeOk || !extensionOk) {
      throw new BusinessException(
          BookErrorCode.INVALID_IMAGE, "형식: " + contentType + " / " + extension);
    }
  }
}
