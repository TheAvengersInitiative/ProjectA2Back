package com.a2.backend.entity;

import com.a2.backend.model.DiscussionDTO;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class Discussion {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    @NotEmpty
    @Size(min = 3, max = 32)
    private String title;

    @NotNull
    @NotEmpty
    @Size(min = 10, max = 750)
    private String body;

    @ManyToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @NotNull
    @NotEmpty
    @Size(min = 1, max = 5)
    private List<ForumTag> forumTags;

    @ManyToOne
    @LazyCollection(LazyCollectionOption.FALSE)
    @NotNull
    @JsonBackReference
    @ToString.Exclude
    private Project project;

    @OneToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @NotNull
    private List<Comment> comments;

    @ManyToOne(cascade = {CascadeType.MERGE})
    private User owner;

    @JsonIgnore private boolean isActive = true;

    public DiscussionDTO toDTO() {
        return DiscussionDTO.builder()
                .project(project)
                .body(body)
                .owner(owner)
                .id(id)
                .title(title)
                .forumTags(forumTags)
                .comments(
                        comments.stream()
                                .filter(Comment::isActive)
                                .map(Comment::toDTO)
                                .collect(Collectors.toList()))
                .build();
    }
}
