package com.a2.backend.entity;

import com.a2.backend.constants.NotificationType;
import com.a2.backend.model.NotificationDTO;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

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

    @NotNull
    private NotificationType type;

    // These are the users to be notified
    @ManyToMany
    @LazyCollection(LazyCollectionOption.FALSE)
    @NotEmpty
    private List<User> users;

    @ManyToOne
    private Project project;

    @ManyToOne
    private Discussion discussion;

    @ManyToOne
    private Comment comment;

    // This is a user related to the notification (e.g., "${user} wrote a discussion on ${project}")
    @ManyToOne
    private User user;

    @NotNull
    @Builder.Default
    private boolean seen = false;

    public NotificationDTO toDTO() {
        return NotificationDTO.builder()
                .id(id)
                .type(type)
                .user(user != null ? user.toDTO() : null)
                .project(project != null ? project.toDTO() : null)
                .discussion(discussion != null ? discussion.toDTO() : null)
                .comment(comment != null ? comment.toDTO() : null)
                .build();
    }
}
