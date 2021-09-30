package com.a2.backend.model;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUserDTO {

    private UUID id;

    private String nickname;

    private String email;
}
