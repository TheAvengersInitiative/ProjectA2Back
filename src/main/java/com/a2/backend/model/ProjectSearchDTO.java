package com.a2.backend.model;

import java.util.List;
import javax.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectSearchDTO {

    @Size(max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Size(max = 5, message = "Number of tags must be between 1 and 5")
    private List<@Size(min = 1, max = 24) String> tags;

    @Size(max = 3, message = "Number of languages must be between 1 and 3")
    private List<String> languages;

    @Builder.Default private int page = -1;

    @Builder.Default private boolean featured = false;
}
