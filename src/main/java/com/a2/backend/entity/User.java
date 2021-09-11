package com.a2.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
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

    @JsonIgnore
    String confirmationToken;

    @Builder.Default
    boolean isActive = false;
}
