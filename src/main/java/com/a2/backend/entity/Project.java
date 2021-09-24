package com.a2.backend.entity;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
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
}
