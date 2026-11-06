package com.sprint.deokhugam.review.application.provided.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.sprint.deokhugam.review.application.required.ReviewLikeRepositoryPort;
import com.sprint.deokhugam.review.application.required.ReviewRepositoryPort;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReviewLikeCommandServiceTest {

  @Mock ReviewRepositoryPort reviewRepositoryPort;
  @Mock ReviewLikeRepositoryPort reviewLikeRepositoryPort;
  @InjectMocks ReviewLikeCommandService service;

  @Test
  void 탈퇴_사용자의_좋아요는_카운터를_반영한_뒤_삭제한다() {
    // given
    UUID userId = UUID.randomUUID();
    when(reviewLikeRepositoryPort.deleteAllByUserId(userId)).thenReturn(2L);

    // when
    long deleted = service.deleteLikesByUserId(userId);

    // then
    assertThat(deleted).isEqualTo(2L);
    InOrder order = inOrder(reviewRepositoryPort, reviewLikeRepositoryPort);
    order.verify(reviewRepositoryPort).decreaseLikeCountsByUserId(userId);
    order.verify(reviewLikeRepositoryPort).deleteAllByUserId(userId);
  }

  @Test
  void 카운터_반영이_실패하면_좋아요를_삭제하지_않고_예외를_전달한다() {
    // given
    UUID userId = UUID.randomUUID();
    RuntimeException failure = new IllegalStateException("카운터 반영 실패");
    doThrow(failure).when(reviewRepositoryPort).decreaseLikeCountsByUserId(userId);

    // when
    RuntimeException exception =
        assertThrows(RuntimeException.class, () -> service.deleteLikesByUserId(userId));

    // then
    assertThat(exception).isSameAs(failure);
    verifyNoInteractions(reviewLikeRepositoryPort);
  }
}
