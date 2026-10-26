package com.sprint.deokhugam.notification.adapter.persistence;

import com.sprint.deokhugam.notification.domain.Notification;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationJpaRepository extends JpaRepository<Notification, UUID> {

  List<Notification> findAllByUser_Id(UUID userId);
}
