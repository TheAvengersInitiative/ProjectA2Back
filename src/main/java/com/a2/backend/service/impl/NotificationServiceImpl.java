package com.a2.backend.service.impl;

import com.a2.backend.entity.Notification;
import com.a2.backend.entity.User;
import com.a2.backend.model.NotificationCreateDTO;
import com.a2.backend.model.NotificationDTO;
import com.a2.backend.repository.NotificationRepository;
import com.a2.backend.service.NotificationService;
import com.a2.backend.service.UserService;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import lombok.val;
import org.springframework.stereotype.Service;

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
}
