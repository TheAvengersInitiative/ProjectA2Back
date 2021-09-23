package com.a2.backend.model;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCreateDTO {
    private static final String linkRegex =
            "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)";

    @NotNull
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @NotNull
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String description;

    @NotNull
    @NotEmpty
    @Size(min = 1, max = 5, message = "Number of links must be between 1 and 5")
    @UniqueElements
    private List<@Pattern(regexp = linkRegex) String> links;

    @NotNull
    @NotEmpty
    @Size(min = 1, max = 5, message = "Number of tags must be between 1 and 5")
    @UniqueElements
    private List<@Pattern(regexp = "[a-zA-Z0-9]+") @Size(min = 1, max = 24,message = "Tag name must be between 1 and 24 characters") String> tags;

    @Size(min = 1, max = 3, message = "Number of languages must be between 1 and 3")
    private List<String> languages;
}
