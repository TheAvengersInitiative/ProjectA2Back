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
public class CommentDTO {

    private UUID id;

    private ProjectUserDTO user;

    private String comment;

    private LocalDateTime date;
}
