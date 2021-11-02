package com.a2.backend.service.impl;

import com.a2.backend.entity.Notification;
import com.a2.backend.entity.User;
import com.a2.backend.exception.InvalidUserException;
import com.a2.backend.exception.NotificationNotFoundException;
import com.a2.backend.model.NotificationCreateDTO;
import com.a2.backend.model.NotificationDTO;
import com.a2.backend.repository.NotificationRepository;
import com.a2.backend.service.NotificationService;
import com.a2.backend.service.UserService;
import lombok.val;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository, UserService userService) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
    }

    @Override
    public NotificationDTO createNotification(NotificationCreateDTO notificationCreateDTO) {
        Notification notification =
                Notification.builder()
                        .userToNotify(notificationCreateDTO.getUserToNotify())
                        .comment(notificationCreateDTO.getComment())
                        .discussion(notificationCreateDTO.getDiscussion())
                        .project(notificationCreateDTO.getProject())
                        .type(notificationCreateDTO.getType())
                        .user(notificationCreateDTO.getUser())
                        .date(LocalDateTime.now())
                        .build();
        return notificationRepository.save(notification).toDTO();
    }

    @Override
    public List<NotificationDTO> getNotificationsOfLoggedUser() {
        User loggedUser = userService.getLoggedUser();

        val notifications = notificationRepository.findAllByUserToNotify(loggedUser);
        if (notifications.size() > 1) {
            notifications.sort(Comparator.comparing(Notification::getDate));
            Collections.reverse(notifications);
        }
        return notifications.stream().map(Notification::toDTO).collect(Collectors.toList());
    }

    @Override
    public NotificationDTO markNotificationAsSeen(UUID id) {
        val notificationOptional = notificationRepository.findById(id);
        if (notificationOptional.isEmpty()) {
            throw new NotificationNotFoundException(
                    String.format("Notification with id %s not found", id));
        }

        val notification = notificationOptional.get();

        if (!userService.getLoggedUser().equals(notification.getUserToNotify())) {
            throw new InvalidUserException("User is not allowed to mark this notification as seen");
        }

        notification.setSeen(true);
        return notificationRepository.save(notification).toDTO();
    }
}
