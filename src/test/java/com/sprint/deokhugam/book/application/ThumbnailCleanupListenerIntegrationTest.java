package com.sprint.deokhugam.book.application;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.sprint.deokhugam.book.application.required.ThumbnailStoragePort;
import com.sprint.deokhugam.book.domain.ThumbnailChangedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.support.TransactionTemplate;

// 리스너가 정말 커밋 뒤, 롤백 뒤에 도는지만 본다. DB에 쓰는 것은 없다
@SpringBootTest
@ActiveProfiles("test")
class ThumbnailCleanupListenerIntegrationTest {

  @Autowired private TransactionTemplate transactionTemplate;
  @Autowired private ApplicationEventPublisher eventPublisher;
  @MockitoBean private ThumbnailStoragePort thumbnailStoragePort;

  private static final ThumbnailChangedEvent EVENT =
      new ThumbnailChangedEvent("books/new.png", "books/old.png");

  // (1-1)
  @Test
  @DisplayName("트랜잭션이 커밋되면 이전 파일만 지운다")
  void afterCommit() {
    transactionTemplate.executeWithoutResult(status -> eventPublisher.publishEvent(EVENT));

    verify(thumbnailStoragePort).delete("books/old.png");
    verify(thumbnailStoragePort, never()).delete("books/new.png");
  }

  // (1-2)
  @Test
  @DisplayName("트랜잭션이 롤백되면 새 파일만 지운다")
  void afterRollback() {
    transactionTemplate.executeWithoutResult(
        status -> {
          eventPublisher.publishEvent(EVENT);
          status.setRollbackOnly();
        });

    verify(thumbnailStoragePort).delete("books/new.png");
    verify(thumbnailStoragePort, never()).delete("books/old.png");
  }
}
