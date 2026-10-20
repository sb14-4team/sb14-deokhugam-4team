package com.sprint.deokhugam.user.adapter.persistence;

import com.sprint.deokhugam.user.domain.User;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, UUID> {}
