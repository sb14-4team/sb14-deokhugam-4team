package com.sprint.deokhugam.user.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public record Password(
    String password
) {

}
