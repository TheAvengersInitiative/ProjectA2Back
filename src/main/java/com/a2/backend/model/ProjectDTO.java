package com.a2.backend.model;

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

    private List<String> tags;

    private List<String> languages;

    private boolean featured;

    private ProjectUserDTO owner;

    private List<ProjectUserDTO> collaborators;

    private List<ProjectUserDTO> applicants;

    private List<ProjectUserDTO> rejectedApplicants;
}
