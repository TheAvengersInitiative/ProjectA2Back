package com.a2.backend.model;

import com.a2.backend.constants.NotificationType;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    private UUID id;

    @NotNull
    private NotificationType type;

    private ProjectDTO project;

    private DiscussionDTO discussion;

    private CommentDTO comment;

    private ProjectUserDTO user;

    private boolean seen;
}
