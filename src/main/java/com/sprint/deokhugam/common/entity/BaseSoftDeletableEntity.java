package com.sprint.deokhugam.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import lombok.Getter;

@MappedSuperclass
@Getter
public abstract class BaseSoftDeletableEntity extends BaseUpdatableEntity {

  // null이면 삭제 안 됨. 물리 삭제(1일 뒤) 기준 시각
  @Column(name = "deleted_at")
  private Instant deletedAt;

  public void softDelete() {
    if (isDeleted()) {
      throw new IllegalStateException("이미 삭제된 데이터입니다.");
    }
    this.deletedAt = Instant.now();
  }

  public boolean isDeleted() {
    return deletedAt != null;
  }
}
