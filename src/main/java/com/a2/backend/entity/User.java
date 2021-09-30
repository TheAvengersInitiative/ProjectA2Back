package com.a2.backend.entity;

import com.a2.backend.constants.PrivacyConstant;
import com.a2.backend.model.ProjectUserDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true)
    String email;

    @Column(unique = true)
    String nickname;

    String biography;

    @JsonIgnore
    String password; // Hashed

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<String> preferredTags;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<String> preferredLanguages;

    @Builder.Default
    private PrivacyConstant tagsPrivacy = PrivacyConstant.PUBLIC;

    @Builder.Default
    private PrivacyConstant languagesPrivacy = PrivacyConstant.PUBLIC;

    @Builder.Default
    private PrivacyConstant ownedProjectsPrivacy = PrivacyConstant.PUBLIC;

    @Builder.Default
    private PrivacyConstant collaboratedProjectsPrivacy = PrivacyConstant.PUBLIC;

    @JsonIgnore
    String confirmationToken;

    @JsonIgnore
    String passwordRecoveryToken;

    @Builder.Default
    boolean isActive = false;

    public ProjectUserDTO toDTO() {
        return ProjectUserDTO.builder().id(id).nickname(nickname).email(email).build();
    }
}
