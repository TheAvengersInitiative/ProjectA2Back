package com.a2.backend.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {

    private UUID id;

    private ProjectUserDTO collaborator;

    private String comment;

    private int score;

    private LocalDateTime date;
}
