package com.a2.backend.model;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.*;
import org.springframework.lang.Nullable;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {

    @NotNull String nickname;

    @NotNull String biography;

    @Nullable List<String> preferredTags;

    @Nullable List<String> preferredLanguages;

    @Nullable List<ProjectDTO> ownedProjects;

    @Nullable List<ProjectDTO> collaboratedProjects;
}
