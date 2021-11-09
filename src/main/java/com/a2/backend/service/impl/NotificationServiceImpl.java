package com.a2.backend.service.impl;

import com.a2.backend.entity.Notification;
import com.a2.backend.entity.User;
import com.a2.backend.exception.InvalidUserException;
import com.a2.backend.exception.NotificationNotFoundException;
import com.a2.backend.model.NotificationCreateDTO;
import com.a2.backend.model.NotificationDTO;
import com.a2.backend.repository.NotificationRepository;
import com.a2.backend.service.MailService;
import com.a2.backend.service.NotificationService;
import com.a2.backend.service.UserService;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final MailService mailService;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            UserService userService,
            MailService mailService) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
        this.mailService = mailService;
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
        NotificationDTO savedNotification = notificationRepository.save(notification).toDTO();
        //        mailService.sendNotificationMail(savedNotification);
        return savedNotification;
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

    @Override
    public List<NotificationDTO> getFirstFiveNotificationsOfLoggedUser() {
        if (getNotificationsOfLoggedUser().size() >= 5) {
            return getNotificationsOfLoggedUser().subList(0, 5);
        }
        return getNotificationsOfLoggedUser();
    }

    //    @Override
    //    public void sendNotificationMail(NotificationDTO notificationDTO) {
    //        val notification = notificationRepository.findById(notificationDTO.getId());
    //        if (notification.isEmpty()) {
    //            throw new NotificationNotFoundException(
    //                    String.format("Notification with id %s not found",
    // notification.get().getId()));
    //        }
    //        if (notification.get().getUserToNotify().isAllowsNotifications()) {
    //            mailService.sendNotificationMail(notificationDTO);
    //        }
    //    }
}
