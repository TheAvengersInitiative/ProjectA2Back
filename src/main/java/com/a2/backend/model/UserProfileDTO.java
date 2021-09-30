package com.a2.backend.model;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {

    @NotNull String nickname;

    @NotNull String biography;

    @Nullable
    List<String> preferredTags;

    @Nullable
    List<String> preferredLanguages;

    @Nullable
    List<ProjectDTO> ownedProjects;

    @Nullable
    List<ProjectDTO> collaboratedProjects;
}
