package com.a2.backend.model;

import com.a2.backend.entity.ForumTag;
import com.a2.backend.entity.Project;
import com.a2.backend.entity.User;
import java.util.List;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscussionDTO {
    private String title;
    private String body;
    private UUID id;
    private List<ForumTag> forumTags;
    private User owner;
    private Project project;
    private List<CommentDTO> comments;
}
