package com.a2.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.UUID;
import javax.persistence.*;
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
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true)
    String email;

    @Column(unique = true)
    String nickname;

    @Column(length = 500)
    String biography;

    @JsonIgnore String password; // Hashed

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<String> preferredTags;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<String> preferredLanguages;

    @JsonIgnore String confirmationToken;

    @JsonIgnore String passwordRecoveryToken;

    @Builder.Default boolean isActive = false;
}
