package com.a2.backend.entity;

import java.io.Serializable;
import java.util.List;
import javax.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
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
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String title;

    private String description;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<String> links;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<String> tags;

    private String owner;
}
