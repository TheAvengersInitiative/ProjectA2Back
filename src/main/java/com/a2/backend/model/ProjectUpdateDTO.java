package com.a2.backend.model;

import javax.persistence.Transient;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUpdateDTO {

    private String title;

    private String description;

    @Transient private String[] links;

    @Transient private String[] tags;
}
