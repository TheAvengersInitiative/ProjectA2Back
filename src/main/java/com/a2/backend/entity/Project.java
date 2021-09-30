package com.a2.backend.entity;

import com.a2.backend.model.ProjectDTO;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.*;
import javax.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Project implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String title;

    @Builder.Default private boolean featured = false;

    private String description;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @NotNull
    @NotEmpty
    @Size(min = 1, max = 5)
    private List<String> links;

    @ManyToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @NotNull
    @NotEmpty
    @Size(min = 1, max = 5)
    private List<Tag> tags;

    @ManyToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Language> languages;

    @ManyToOne(cascade = {CascadeType.MERGE})
    private User owner;

    @ManyToMany(cascade = CascadeType.MERGE)
    @LazyCollection(LazyCollectionOption.FALSE)
    @NotNull
    private List<User> collaborators;

    @ManyToMany(cascade = CascadeType.MERGE)
    @LazyCollection(LazyCollectionOption.FALSE)
    @NotNull
    private List<User> applicants;

    @ManyToMany(cascade = CascadeType.MERGE)
    @LazyCollection(LazyCollectionOption.FALSE)
    @NotNull
    private List<User> rejectedApplicants;

    public ProjectDTO toDTO() {
        return ProjectDTO.builder()
                .id(id)
                .title(title)
                .featured(featured)
                .languages(languages.stream().map(Language::getName).collect(Collectors.toList()))
                .tags(tags.stream().map(Tag::getName).collect(Collectors.toList()))
                .description(description)
                .links(links)
                .owner(owner.toDTO())
                .collaborators(collaborators.stream().map(User::toDTO).collect(Collectors.toList()))
                .applicants(applicants.stream().map(User::toDTO).collect(Collectors.toList()))
                .rejectedApplicants(
                        rejectedApplicants.stream().map(User::toDTO).collect(Collectors.toList()))
                .build();
    }
}
