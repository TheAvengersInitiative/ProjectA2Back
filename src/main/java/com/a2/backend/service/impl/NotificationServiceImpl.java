package com.a2.backend.service.impl;

import com.a2.backend.entity.Notification;
import com.a2.backend.model.NotificationCreateDTO;
import com.a2.backend.model.NotificationDTO;
import com.a2.backend.repository.NotificationRepository;
import com.a2.backend.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public NotificationDTO createNotification(NotificationCreateDTO notificationCreateDTO) {
        Notification notification =
                Notification.builder()
                        .users(notificationCreateDTO.getUsers())
                        .comment(notificationCreateDTO.getComment())
                        .discussion(notificationCreateDTO.getDiscussion())
                        .project(notificationCreateDTO.getProject())
                        .type(notificationCreateDTO.getType())
                        .user(notificationCreateDTO.getUser())
                        .build();
        return notificationRepository.save(notification).toDTO();
    }
}
