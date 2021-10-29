package com.a2.backend.model;

import com.a2.backend.constants.NotificationType;
import com.a2.backend.entity.Comment;
import com.a2.backend.entity.Discussion;
import com.a2.backend.entity.Project;
import com.a2.backend.entity.User;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCreateDTO {

    @NotNull
    private NotificationType type;

    @NotEmpty
    private List<User> users;

    private Project project;

    private Discussion discussion;

    private Comment comment;

    private User user;
}
