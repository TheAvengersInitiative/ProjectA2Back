package com.a2.backend.model;

import com.a2.backend.entity.Language;
import com.a2.backend.entity.Tag;
import lombok.*;

import java.util.List;
import java.util.UUID;

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

    private List<String> forumTags;

    private List<Language> languages;

    private boolean featured;

    private ProjectUserDTO owner;

    private List<ProjectUserDTO> collaborators;

    private List<ProjectUserDTO> applicants;
}
