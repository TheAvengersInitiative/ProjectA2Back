package com.a2.backend.model;

import com.a2.backend.constants.NotificationType;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    private UUID id;

    @NotNull private NotificationType type;

    private ProjectDTO project;

    private DiscussionDTO discussion;

    private CommentDTO comment;

    private ProjectUserDTO user;

    private ProjectUserDTO userToNotify;

    private LocalDateTime date;

    private boolean seen;
}
