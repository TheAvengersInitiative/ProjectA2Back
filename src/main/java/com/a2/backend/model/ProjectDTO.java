package com.a2.backend.model;

import com.a2.backend.entity.Discussion;
import com.a2.backend.entity.ForumTag;
import com.a2.backend.entity.Language;
import com.a2.backend.entity.Tag;
import java.util.List;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {

    private UUID id;

    private String title;

    private String description;

    private List<String> links;

    private List<Tag> tags;

    private List<ForumTag> forumTags;

    private List<Language> languages;

    private boolean featured;

    private ProjectUserDTO owner;

    private List<ProjectUserDTO> collaborators;

    private List<ProjectUserDTO> applicants;

    private List<ReviewDTO> reviews;

    private List<DiscussionDTO> discussions;
}
