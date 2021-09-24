package com.a2.backend.model;

import com.a2.backend.entity.User;
import java.util.List;
import javax.validation.constraints.Size;
import lombok.*;

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

    @Size(min = 1, max = 5, message = "Number of links must be between 1 and 5")
    private List<String> links;

    @Size(min = 1, max = 5, message = "Number of tags must be between 1 and 5")
    private List<@Size(min = 1, max = 24) String> tags;

    @Size(min = 1, max = 3, message = "Number of languages must be between 1 and 3")
    private List<String> languages;

    @Builder.Default private boolean featured = false;

    @NonNull private User owner;
}
