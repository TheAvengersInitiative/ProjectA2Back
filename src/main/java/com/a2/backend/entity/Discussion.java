package com.a2.backend.entity;

import com.a2.backend.model.DiscussionDTO;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.util.List;
import java.util.UUID;
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
    private Project project;

    @OneToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @NotNull
    private List<Comment> comments;

    @ManyToOne(cascade = {CascadeType.MERGE})
    private User owner;

    public DiscussionDTO toDTO() {
        return DiscussionDTO.builder()
                .project(project)
                .owner(owner)
                .id(id)
                .title(title)
                .forumTags(forumTags)
                .build();
    }
}
