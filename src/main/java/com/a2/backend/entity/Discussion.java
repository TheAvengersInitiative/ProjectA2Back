package com.a2.backend.entity;

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
    private List<Tag> tags;

    @ManyToOne
    @LazyCollection(LazyCollectionOption.FALSE)
    @NotNull
    private Project project;
}
