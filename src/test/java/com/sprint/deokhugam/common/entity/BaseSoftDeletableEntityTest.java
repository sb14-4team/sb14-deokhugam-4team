package com.sprint.deokhugam.common.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BaseSoftDeletableEntityTest {

  @Test
  @DisplayName("새로 만든 데이터는 삭제되지 않은 상태다")
  void notDeletedByDefault() {
    BaseSoftDeletableEntity entity = new BaseSoftDeletableEntity() {};

    assertThat(entity.isDeleted()).isFalse();
    assertThat(entity.getDeletedAt()).isNull();
  }

  @Test
  @DisplayName("softDelete를 호출하면 삭제 시각이 기록된다")
  void softDelete() {
    BaseSoftDeletableEntity entity = new BaseSoftDeletableEntity() {};

    entity.softDelete();

    assertThat(entity.isDeleted()).isTrue();
    assertThat(entity.getDeletedAt()).isNotNull();
  }

  @Test
  @DisplayName("이미 삭제된 데이터를 다시 삭제하면 예외가 발생한다")
  void softDeleteTwice() {
    BaseSoftDeletableEntity entity = new BaseSoftDeletableEntity() {};
    entity.softDelete();

    assertThatThrownBy(entity::softDelete).isInstanceOf(IllegalStateException.class);
  }
}
