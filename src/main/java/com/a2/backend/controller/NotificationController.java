package com.a2.backend.controller;

import com.a2.backend.constants.SecurityConstants;
import com.a2.backend.service.NotificationService;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Secured({SecurityConstants.USER_ROLE})
    @GetMapping
    public ResponseEntity<?> getNotificationsOfLoggedUser() {
        val userNotifications = notificationService.getNotificationsOfLoggedUser();
        return ResponseEntity.status(HttpStatus.OK).body(userNotifications);
    }

    @Secured({SecurityConstants.USER_ROLE})
    @PutMapping("/{id}")
    public ResponseEntity<?> updateNotification(@PathVariable("id") UUID id) {
        val notification = notificationService.markNotificationAsSeen(id);
        return ResponseEntity.status(HttpStatus.OK).body(notification);
    }

    @Secured({SecurityConstants.USER_ROLE})
    @GetMapping("/first-five")
    public ResponseEntity<?> getFirstFiveNotificationsOfLoggedUser() {
        val userNotifications = notificationService.getFirstFiveNotificationsOfLoggedUser();
        return ResponseEntity.status(HttpStatus.OK).body(userNotifications);
    }
}
