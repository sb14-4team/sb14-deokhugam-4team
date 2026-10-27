package com.sprint.deokhugam.book.application;

import com.sprint.deokhugam.book.application.dto.BookCreateRequest;
import com.sprint.deokhugam.book.application.dto.BookDto;
import com.sprint.deokhugam.book.application.dto.BookUpdateRequest;
import com.sprint.deokhugam.book.application.provided.command.BookCommand;
import com.sprint.deokhugam.book.application.required.BookRepositoryPort;
import com.sprint.deokhugam.book.application.required.ThumbnailStoragePort;
import com.sprint.deokhugam.book.domain.Book;
import com.sprint.deokhugam.common.exception.BusinessException;
import com.sprint.deokhugam.domain.book.exception.BookErrorCode;
import java.io.IOException;
import java.net.URI;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class BookCommandService implements BookCommand {

  private final BookRepositoryPort bookRepositoryPort;
  private final ThumbnailStoragePort thumbnailStoragePort;

  private static final long MAX_THUMBNAIL_BYTES = 5_000_000;
  private static final Set<String> IMAGE_TYPES = Set.of("image/jpeg", "image/png");
  private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png");

  @Override
  public BookDto create(BookCreateRequest request, MultipartFile thumbnailImage) {
    String isbn = request.isbn();
    if (isbn != null && bookRepositoryPort.existsByIsbn(isbn)) {
      throw new BusinessException(BookErrorCode.DUPLICATE_ISBN, "isbn=" + isbn);
    }

    // 파일을 먼저 올린 뒤 도서를 저장한다
    String thumbnailUrl = thumbnailImage == null ? null : storeThumbnail(thumbnailImage, null);
    Book book =
        Book.create(
            request.title(),
            request.author(),
            request.description(),
            request.publisher(),
            request.publishedDate(),
            isbn,
            thumbnailUrl);
    return BookDto.from(bookRepositoryPort.save(book));
  }

  @Override
  public BookDto update(UUID bookId, BookUpdateRequest request, MultipartFile thumbnailImage) {
    Book book =
        bookRepositoryPort
            .findActiveById(bookId)
            .orElseThrow(() -> new BusinessException(BookErrorCode.NOT_FOUND, "bookId=" + bookId));

    // 트랜잭션이 끝날 때 JPA가 바뀐 값을 알아서 UPDATE 한다 (변경 감지). save()를 따로 부르지 않는다
    book.update(
        request.title(),
        request.author(),
        request.description(),
        request.publisher(),
        request.publishedDate());
    if (thumbnailImage != null) {
      book.changeThumbnailUrl(storeThumbnail(thumbnailImage, book.getThumbnailUrl()));
    }
    return BookDto.from(book);
  }

  // 등록이면 previousUrl은 null
  private String storeThumbnail(MultipartFile image, String previousUrl) {
    validateThumbnail(image);
    String key = thumbnailKey(image);

    byte[] bytes;
    try {
      bytes = image.getBytes();
    } catch (IOException e) {
      throw new BusinessException(BookErrorCode.INVALID_IMAGE, e);
    }

    String url = thumbnailStoragePort.upload(key, bytes, image.getContentType());
    registerCleanup(key, previousUrl);
    return url;
  }

  private void validateThumbnail(MultipartFile image) {
    if (image.isEmpty()) {
      throw new BusinessException(BookErrorCode.INVALID_IMAGE, "빈 파일");
    }
    if (image.getSize() > MAX_THUMBNAIL_BYTES) {
      throw new BusinessException(BookErrorCode.INVALID_IMAGE, "크기 초과: " + image.getSize());
    }
    String contentType = image.getContentType();
    String extension = StringUtils.getFilenameExtension(image.getOriginalFilename());
    boolean typeOk = contentType != null && IMAGE_TYPES.contains(contentType);
    boolean extensionOk = extension != null && IMAGE_EXTENSIONS.contains(extension.toLowerCase());
    if (!typeOk || !extensionOk) {
      throw new BusinessException(
          BookErrorCode.INVALID_IMAGE, "형식: " + contentType + " / " + extension);
    }
  }

  private String thumbnailKey(MultipartFile image) {
    String extension = StringUtils.getFilenameExtension(image.getOriginalFilename()).toLowerCase();
    return "books/" + UUID.randomUUID() + "." + extension;
  }

  // 파일은 롤백이 안 되니 커밋 결과를 본 뒤에 정리한다
  private void registerCleanup(String newKey, String previousUrl) {
    TransactionSynchronizationManager.registerSynchronization(
        new TransactionSynchronization() {
          @Override
          public void afterCommit() {
            if (previousUrl != null) {
              thumbnailStoragePort.delete(keyOf(previousUrl));
            }
          }

          @Override
          public void afterCompletion(int status) {
            if (status != STATUS_COMMITTED) {
              thumbnailStoragePort.delete(newKey);
            }
          }
        });
  }

  // DB의 썸네일 URL은 늘 우리 버킷 URL이라 경로가 곧 키다
  private String keyOf(String url) {
    return URI.create(url).getPath().substring(1);
  }
}
