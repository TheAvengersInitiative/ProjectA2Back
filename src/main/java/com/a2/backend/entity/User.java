package com.a2.backend.entity;

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

    String password; // Hashed

    @Builder.Default
    boolean isActive = false;
}