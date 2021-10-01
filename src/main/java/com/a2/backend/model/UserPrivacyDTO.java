package com.a2.backend.model;

import com.a2.backend.constants.PrivacyConstant;
import lombok.*;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPrivacyDTO {

    @NotNull
    private PrivacyConstant tagsPrivacy;

    @NotNull
    private PrivacyConstant languagesPrivacy;

    @NotNull
    private PrivacyConstant ownedProjectsPrivacy;

    @NotNull
    private PrivacyConstant collaboratedProjectsPrivacy;
}
