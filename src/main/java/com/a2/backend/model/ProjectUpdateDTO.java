package com.a2.backend.model;

import lombok.*;

import javax.persistence.Transient;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectUpdateDTO {

    private String title;

    private String description;

    @Transient
    private String[] links;

    @Transient
    private String[] tags;

    private String owner;
}
