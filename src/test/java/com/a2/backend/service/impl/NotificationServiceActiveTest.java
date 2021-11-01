package com.a2.backend.service.impl;

import static org.junit.jupiter.api.Assertions.*;

import com.a2.backend.constants.NotificationType;
import com.a2.backend.model.NotificationCreateDTO;
import com.a2.backend.model.NotificationDTO;
import com.a2.backend.repository.NotificationRepository;
import com.a2.backend.repository.ProjectRepository;
import com.a2.backend.service.NotificationService;
import com.a2.backend.service.UserService;
import java.util.List;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

public class NotificationServiceActiveTest extends AbstractServiceTest {

    @Autowired NotificationService notificationService;

    @Autowired UserService userService;

    @Autowired ProjectRepository projectRepository;

    @Autowired NotificationRepository notificationRepository;

    @Test
    @WithMockUser("rodrigo.pazos@ing.austral.edu.ar")
    void Test001_NotificationServiceWhenCreatingNotificationWithValidDTOThenItIsCreated() {
        val project = projectRepository.findByTitle("Django").get();

        NotificationCreateDTO notificationCreateDTO =
                NotificationCreateDTO.builder()
                        .userToNotify(userService.getLoggedUser())
                        .type(NotificationType.REVIEW)
                        .project(project)
                        .user(project.getOwner())
                        .discussion(project.getDiscussions().get(0))
                        .comment(project.getDiscussions().get(0).getComments().get(0))
                        .build();

        NotificationDTO notification =
                notificationService.createNotification(notificationCreateDTO);

        assertNotNull(notification.getId());
        assertEquals(NotificationType.REVIEW, notification.getType());
        assertEquals(project.toDTO().getId(), notification.getProject().getId());
        assertEquals(project.getOwner().toDTO().getId(), notification.getUser().getId());
        assertEquals(
                project.getDiscussions().get(0).toDTO().getId(),
                notification.getDiscussion().getId());
        assertEquals(
                project.getDiscussions().get(0).getComments().get(0).toDTO().getId(),
                notification.getComment().getId());
        assertFalse(notification.isSeen());
    }

    @Test
    @WithMockUser("rodrigo.pazos@ing.austral.edu.ar")
    void Test002_NotificationServiceWhenCreatingNotificationWithAllowedNullFieldsThenItIsCreated() {
        NotificationCreateDTO notificationCreateDTO =
                NotificationCreateDTO.builder()
                        .userToNotify(userService.getLoggedUser())
                        .type(NotificationType.REVIEW)
                        .build();

        NotificationDTO notification =
                notificationService.createNotification(notificationCreateDTO);

        assertNotNull(notification.getId());
        assertEquals(NotificationType.REVIEW, notification.getType());
        assertNull(notification.getProject());
        assertNull(notification.getUser());
        assertNull(notification.getComment());
        assertNull(notification.getDiscussion());
        assertFalse(notification.isSeen());
    }

    @Test
    @WithMockUser(username = "agustin.ayerza@ing.austral.edu.ar")
    void Test003_NotificationServiceShouldReturnAllLoggedUsersNotificationsOrderedByDate() {
        val notifications = notificationService.getNotificationsOfLoggedUser();
        assertEquals(3, notifications.size());
        assertTrue(notifications.get(0).getDate().isAfter(notifications.get(1).getDate()));
        assertTrue(notifications.get(1).getDate().isAfter(notifications.get(2).getDate()));
    }
}
