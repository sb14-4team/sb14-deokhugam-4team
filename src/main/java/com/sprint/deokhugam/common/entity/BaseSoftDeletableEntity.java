package com.sprint.deokhugam.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

@MappedSuperclass
@Getter
public abstract class BaseSoftDeletableEntity extends BaseUpdatableEntity {

  @Column(name = "deleted")
  private boolean deleted = false;

  public void softDelete() {
    if (this.deleted) {
      throw new RuntimeException();
    }
    this.deleted = true;
  }
}
