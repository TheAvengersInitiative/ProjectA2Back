package com.a2.backend.model;

import com.a2.backend.entity.User;
import lombok.*;

import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCreateDTO {

    @NonNull
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String description;

    @Size(min = 1, max = 5)
    private List<String> links;

    @Size(min = 1, max = 5)
    private List<@Size(min = 1, max = 24) String> tags;

    @NonNull
    private User owner;
}
