package com.sprint.deokhugam.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;

@MappedSuperclass
@Getter
public abstract class BaseUpdatableEntity extends BaseEntity {

  @Column(name = "updated_at")
  @LastModifiedDate
  private Instant updated;
}
