package com.sprint.deokhugam.notification.application.provided.command;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface NotificationCleaner {

  void deleteConfirmed(Instant cutoff);

  void deleteByUser(UUID userId);

  void deleteByReview(UUID reviewId);

  void deleteByReviewIds(List<UUID> reviewIds);
}
