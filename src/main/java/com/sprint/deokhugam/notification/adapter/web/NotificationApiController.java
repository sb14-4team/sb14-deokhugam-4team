package com.sprint.deokhugam.notification.adapter.web;

import com.sprint.deokhugam.common.pagination.CursorPageResponse;
import com.sprint.deokhugam.common.web.annotation.RequestHeader;
import com.sprint.deokhugam.notification.application.dto.NotificationDto;
import com.sprint.deokhugam.notification.application.dto.NotificationSearchCond;
import com.sprint.deokhugam.notification.application.provided.command.NotificationCommand;
import com.sprint.deokhugam.notification.application.provided.query.NotificationFinder;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationApiController {

  private final NotificationCommand notificationCommand;
  private final NotificationFinder notificationFinder;

  @PatchMapping("/{notificationId}")
  public ResponseEntity<NotificationDto> check(
      @PathVariable UUID notificationId,
      @RequestHeader("Deokhugam-Request-User-ID") UUID headerId) {

    NotificationDto confirm = notificationCommand.confirm(notificationId, headerId);

    return ResponseEntity.ok(confirm);
  }

  @PatchMapping("/read-all")
  public ResponseEntity<Void> readAll(@RequestHeader("Deokhugam-Request-User-ID") UUID headerId) {
    notificationCommand.confirmAll(headerId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping
  public ResponseEntity<CursorPageResponse<NotificationDto>> getAll(
      @RequestParam UUID userId,
      @RequestParam(defaultValue = "DESC") String direction,
      @RequestParam(required = false) String cursor,
      @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) Instant after,
      @RequestParam(defaultValue = "20") int limit) {

    NotificationSearchCond cond =
        NotificationSearchCond.to(userId, direction, cursor, after, limit);

    CursorPageResponse<NotificationDto> response = notificationFinder.findAllByUser(cond);

    return ResponseEntity.ok(response);
  }
}
