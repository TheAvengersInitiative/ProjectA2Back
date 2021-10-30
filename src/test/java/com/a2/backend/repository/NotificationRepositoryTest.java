package com.a2.backend.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.a2.backend.BackendApplication;
import com.a2.backend.constants.NotificationType;
import com.a2.backend.entity.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@AutoConfigureWebClient
@DataJpaTest
@ExtendWith(SpringExtension.class)
@Import({BackendApplication.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class NotificationRepositoryTest {

    @Autowired UserRepository userRepository;

    @Autowired ProjectRepository projectRepository;

    @Autowired NotificationRepository notificationRepository;

    User user =
            User.builder()
                    .nickname("ropa")
                    .email("some@email.com")
                    .biography("bio")
                    .password("password")
                    .build();

    User anotherUser =
            User.builder()
                    .nickname("peltevis")
                    .email("another@email.com")
                    .biography("bio")
                    .password("password")
                    .build();

    String title = "New project";
    String description = "Testing project repository";
    List<ForumTag> forumTags = Collections.singletonList(ForumTag.builder().name("Start").build());
    List<Tag> tags = Collections.singletonList(Tag.builder().name("Start").build());
    List<String> links = Collections.singletonList("http://link.com");
    Project project =
            Project.builder()
                    .title(title)
                    .description(description)
                    .forumTags(forumTags)
                    .links(links)
                    .tags(tags)
                    .owner(anotherUser)
                    .applicants(List.of())
                    .collaborators(List.of())
                    .reviews(List.of())
                    .build();

    Notification notification =
            Notification.builder()
                    .user(anotherUser)
                    .userToNotify(user)
                    .project(project)
                    .type(NotificationType.REVIEW)
                    .date(LocalDateTime.now())
                    .build();

    @Test
    void Test001_NotificationRepositoryShouldSaveNotifications() {
        userRepository.save(user);
        userRepository.save(anotherUser);
        projectRepository.save(project);

        assertTrue(notificationRepository.findAll().isEmpty());

        Notification savedNotification = notificationRepository.save(notification);

        assertEquals(1, notificationRepository.findAllByUserToNotify(user).size());

        assertEquals(NotificationType.REVIEW, savedNotification.getType());
        assertEquals(user.getNickname(), savedNotification.getUserToNotify().getNickname());
        assertEquals(anotherUser.getNickname(), savedNotification.getUser().getNickname());
        assertEquals(project.getTitle(), savedNotification.getProject().getTitle());
    }
}
