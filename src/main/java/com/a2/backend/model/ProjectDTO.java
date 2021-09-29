package com.a2.backend.model;

import com.a2.backend.entity.User;
import java.util.List;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {

    private UUID id;

    private String title;

    private boolean featured = false;

    private String description;

    private List<String> links;

    private List<String> tags;

    private List<String> languages;

    private User owner;
}
