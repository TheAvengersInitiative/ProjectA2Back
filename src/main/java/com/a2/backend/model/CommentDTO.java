package com.a2.backend.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {

    private UUID id;

    private ProjectUserDTO user;

    private String comment;

    private LocalDateTime date;
}
