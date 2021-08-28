package com.a2.backend.entity;

import java.io.Serializable;
import javax.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

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

    @Transient private String[] links;

    @Transient private String[] tags;

    private String owner;
}
