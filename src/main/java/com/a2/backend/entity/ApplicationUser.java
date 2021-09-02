package com.a2.backend.entity;

import java.util.UUID;
import javax.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class ApplicationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true)
    String email;

    @Column(unique = true)
    String nickname;

    String biography;

    String password; // Hashed

    @Builder.Default boolean isActive = false;
}
