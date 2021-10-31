package com.a2.backend.entity;

import com.a2.backend.constants.NotificationType;
import com.a2.backend.model.NotificationDTO;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull private NotificationType type;

    // These are the users to be notified
    @ManyToOne @NotNull private User userToNotify;

    @ManyToOne private Project project;

    @ManyToOne private Discussion discussion;

    @ManyToOne private Comment comment;

    // This is a user related to the notification (e.g., "${user} wrote a discussion on ${project}")
    @ManyToOne private User user;

    @NotNull @Builder.Default private boolean seen = false;

    @NotNull private LocalDateTime date;

    public NotificationDTO toDTO() {
        return NotificationDTO.builder()
                .id(id)
                .type(type)
                .user(user != null ? user.toDTO() : null)
                .userToNotify(userToNotify.toDTO())
                .project(project != null ? project.toDTO() : null)
                .discussion(discussion != null ? discussion.toDTO() : null)
                .comment(comment != null ? comment.toDTO() : null)
                .date(date)
                .seen(seen)
                .build();
    }
}
