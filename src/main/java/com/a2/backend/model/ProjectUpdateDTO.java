package com.a2.backend.model;

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

    private String[] links;

    private String[] tags;
}
