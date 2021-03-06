package com.a2.backend.entity;

import com.a2.backend.model.ProjectDTO;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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

    @OneToMany(
            cascade = {
                CascadeType.PERSIST,
                CascadeType.DETACH,
                CascadeType.MERGE,
                CascadeType.REFRESH
            })
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonManagedReference
    private List<Discussion> discussions;

    @ManyToMany(
            cascade = {
                CascadeType.PERSIST,
                CascadeType.DETACH,
                CascadeType.MERGE,
                CascadeType.REFRESH
            })
    @LazyCollection(LazyCollectionOption.FALSE)
    @NotNull
    @NotEmpty
    @Size(min = 1, max = 5)
    private List<Tag> tags;

    @ManyToMany(
            cascade = {
                CascadeType.PERSIST,
                CascadeType.DETACH,
                CascadeType.MERGE,
                CascadeType.REFRESH
            })
    @LazyCollection(LazyCollectionOption.FALSE)
    @NotNull
    @NotEmpty
    @Size(min = 1, max = 5)
    private List<ForumTag> forumTags;

    @ManyToMany(
            cascade = {
                CascadeType.PERSIST,
                CascadeType.DETACH,
                CascadeType.MERGE,
                CascadeType.REFRESH
            })
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

    @OneToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @NotNull
    private List<Review> reviews;

    public ProjectDTO toDTO() {
        return ProjectDTO.builder()
                .id(id)
                .discussions(
                        discussions.stream()
                                .filter(Discussion::isActive)
                                .map(Discussion::toDTO)
                                .collect(Collectors.toList()))
                .title(title)
                .featured(featured)
                .languages(languages)
                .tags(tags)
                .forumTags(forumTags)
                .description(description)
                .links(links)
                .owner(owner.toDTO())
                .collaborators(collaborators.stream().map(User::toDTO).collect(Collectors.toList()))
                .applicants(applicants.stream().map(User::toDTO).collect(Collectors.toList()))
                .reviews(reviews.stream().map(Review::toDTO).collect(Collectors.toList()))
                .build();
    }
}
