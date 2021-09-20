package com.a2.backend.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCreateDTO {

    @NotNull
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @NotNull
    @Size(min = 10, max = 500, message = "Description must be between 10 and 500 characters")
    private String description;

    @NotNull
    @NotEmpty
    @Size(min = 1, max = 5)
    private String[] links;

    @NotNull
    @NotEmpty
    @Size(min = 1, max = 5)
    private String[] tags;
}
